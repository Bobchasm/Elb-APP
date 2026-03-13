package com.tju.elm.business.service.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tju.elm.api.client.NotificationClient;
import com.tju.elm.api.client.OrderClient;
import com.tju.elm.api.client.UserClient;
import com.tju.elm.api.dto.NotificationSendDTO;
import com.tju.elm.api.dto.WebSocketPushDTO;
import com.tju.elm.api.po.User;
import com.tju.elm.business.mapper.BusinessMapper;
import com.tju.elm.business.mapper.MerchantInteractionMapper;
import com.tju.elm.business.pojo.dto.BusinessDTO;
import com.tju.elm.business.pojo.dto.BusinessPermissionDTO;
import com.tju.elm.business.pojo.dto.BusinessUpdateDTO;
import com.tju.elm.business.pojo.entity.Business;
import com.tju.elm.business.pojo.vo.BusinessPermissionVO;
import com.tju.elm.business.pojo.vo.BusinessSearchVO;
import com.tju.elm.business.pojo.vo.MerchantStatsVO;
import com.tju.elm.business.service.BusinessService;
import com.tju.elm.business.pojo.vo.BusinessVO;
import exception.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import result.ResultCodeEnum;
import utils.UserContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private MerchantInteractionMapper interactionMapper;

    @Autowired
    private final BusinessMapper businessMapper;
    @Autowired
    private UserClient userClient;

    @Autowired
    private NotificationClient notificationClient;
    @Autowired
    private OrderClient orderClient;

    @Override
    public BusinessVO getBusinessById(Long id) {
        //这里需要权限检查吗
//        System.out.println("查询商家ID: " + id);
        return businessMapper.getBusinessById(id);
    }

    @Override
    public BusinessVO updateBusiness(Long id, BusinessUpdateDTO updateDto) {

        User currentUser = userClient.getUserByName(UserContext.getUsername()).getData();

        // 添加 null 检查
        if (currentUser == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }

        // 权限判断 - 检查用户是否有 BUSINESS 或 ADMIN 权限
        boolean hasBusinessPermission = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "BUSINESS".equals(auth.getName()));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getName()));

        // 如果没有 BUSINESS 权限且不是 ADMIN，则抛出权限异常
        if (!hasBusinessPermission && !isAdmin) {
            throw new APIException(ResultCodeEnum.UNAUTHORIZED);
        }
//        //判断是不是自己操作自己的店铺或者管理员
//        boolean isSelf=isIdPresent(businessMapper.getBusinessIdsByUserId(currentUser.getId()),id);
//
//        if(!isSelf&&!isAdmin){
//            throw new APIException(ResultCodeEnum.UNAUTHORIZED);
//        }
////        System.out.println("前端--更新商家信息为: " + updateDto);
//        // 1. 更新商户基本信息
//        int result = businessMapper.updateBusiness(id, updateDto);
//        if (result == 0) {throw new APIException(ResultCodeEnum.BUSINESS_MISSED);}

        //如果是不是管理员，且传入的商铺id不是自己的 isSelf
        boolean isSelf=isIdPresent(businessMapper.getBusinessIdsByUserId(currentUser.getId()),id);
        if(!isSelf&&!isAdmin){
            throw new APIException(ResultCodeEnum.USER_DENIED);
        }
        //如果不是管理员，且传入的businessOwner的username对应的user_id不是自己的--USER_DENIED
        Long ownerId=userClient.getUserByName(updateDto.getBusinessOwner().getUsername()).getData().getId();
        boolean isOwner=ownerId.equals(currentUser.getId());
        //判断是不是管理员 isAdmin
        if(!isOwner&&!isAdmin){
            throw new APIException(ResultCodeEnum.USER_DENIED);
        }
        //执行更新操作（部分更新）
        int result = businessMapper.patchBusiness(id, updateDto);
        //如果是管理员，需要将传入的username对应的user_id传入business表的user_id
        if(isAdmin){
            //根据商铺id更新user_id
            businessMapper.updateUserIdById(ownerId,id);//id是business的商铺id，更新business表的user_id为传入的username对应的user_id
        }
        if (result == 0) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }


        // 2. 如果有商户所有者信息，更新商户所有者
        if (updateDto.getBusinessOwner() != null) {businessMapper.updateBusinessOwner(id, updateDto);}
        // 3. 重新查询完整的商户信息并返回
        return businessMapper.getBusinessById(id);
    }
    @Override
    public BusinessVO deleteBusiness(Long id) {
        User currentUser = userClient.getUserByName(UserContext.getUsername()).getData();

        // 添加 null 检查
        if (currentUser == null) {
            throw new APIException(ResultCodeEnum.USER_MISSED);//用户不存在
        }

        // 权限判断 - 检查用户是否有 BUSINESS 或 ADMIN 权限
        boolean hasBusinessPermission = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "BUSINESS".equals(auth.getName()));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getName()));


        // 如果没有 BUSINESS 权限且不是 ADMIN，则抛出权限异常
        if (!hasBusinessPermission && !isAdmin) {
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);//权限不足
        }
        // 判断是不是自己操作自己的店铺或者管理员
        boolean isSelf=isIdPresent(businessMapper.getBusinessIdsByUserId(currentUser.getId()),id);
        if(!isSelf&&!isAdmin){
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);//权限不足
        }
        BusinessVO businessVo =businessMapper.getBusinessById(id);
        int result =businessMapper.deleteBusiness(id);
        if (result == 0) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);//商铺不存在
        }
        return businessVo;

    }

    public static boolean isIdPresent(List<Long> idList, Long targetId) {
        // 处理空列表情况
        if (idList == null || idList.isEmpty()) {
            return false;
        }
        // 处理目标ID为null的情况
        if (targetId == null) {
            return true;
        }
        return idList.contains(targetId);
    }
    @Override
    public BusinessVO patchBusiness(Long id, BusinessUpdateDTO updateDto) {
        User currentUser = userClient.getUserByName(UserContext.getUsername()).getData();

        // 添加 null 检查
        if (currentUser == null) {
            throw new APIException(ResultCodeEnum.USER_MISSED);
        }

        // 权限判断 - 检查用户是否有 BUSINESS 或 ADMIN 权限
        boolean hasBusinessPermission = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "BUSINESS".equals(auth.getName()));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getName()));

        // 如果没有 BUSINESS 权限且不是 ADMIN，则抛出权限异常
        if (!hasBusinessPermission && !isAdmin) {
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);
        }

        //如果是不是管理员，且传入的商铺id不是自己的 isSelf
        boolean isSelf=isIdPresent(businessMapper.getBusinessIdsByUserId(currentUser.getId()),id);
        if(!isSelf&&!isAdmin){
            throw new APIException(ResultCodeEnum.USER_DENIED);
        }
        //如果不是管理员，且传入的businessOwner的username对应的user_id不是自己的--USER_DENIED
        Long ownerId=userClient.getUserByName(updateDto.getBusinessOwner().getUsername()).getData().getId();
        boolean isOwner=ownerId.equals(currentUser.getId());
        //判断是不是管理员 isAdmin
        if(!isOwner&&!isAdmin){
            throw new APIException(ResultCodeEnum.USER_DENIED);
        }
        //执行更新操作（部分更新）
        int result = businessMapper.patchBusiness(id, updateDto);
        //如果是管理员，需要将传入的username对应的user_id传入business表的user_id
        if(isAdmin){
            //根据商铺id更新user_id
            businessMapper.updateUserIdById(ownerId,id);//id是business的商铺id，更新business表的user_id为传入的username对应的user_id
        }
        if (result == 0) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }

        // 2. 如果有商户所有者信息，更新商户所有者
        if (updateDto.getBusinessOwner() != null) {
            //部分更新
            businessMapper.patchBusinessOwner(id, updateDto);
        }
        return businessMapper.getBusinessById(id);
    }

    @Override
    public BusinessVO addBusiness(BusinessDTO businessDTO) {
        //先查id是否在users表里面
        User currentUser = userClient.getUserByName(UserContext.getUsername()).getData();

        // 添加 null 检查
        if (currentUser == null) {
            throw new APIException(ResultCodeEnum.USER_MISSED);
        }

        // 权限判断 - 检查用户是否有 BUSINESS 或 ADMIN 权限
        boolean hasBusinessPermission = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "BUSINESS".equals(auth.getName()));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getName()));

        // 如果没有 BUSINESS 权限且不是 ADMIN，则抛出权限异常
        if (!hasBusinessPermission && !isAdmin) {
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);
        }
        // 1.是商家：传入的username对应的user_id与currentUser的user_id是否一致
        // 2.是管理员：直接通过
        boolean isSelf=userClient.getUserByName(businessDTO.getBusinessOwner().getUsername()).getData().getId().equals(currentUser.getId());
//        boolean isSelf=isIdPresent(businessMapper.getBusinessIdsByUserId(currentUser.getId()),businessDTO.getId());
        if(!isSelf&&!isAdmin){
            throw new APIException(ResultCodeEnum.USER_DENIED);
        }
        //-----------------------需要调用user的接口------------------!!!

        int result =businessMapper.insertBusiness(businessDTO);
        if (result == 0) {//这不对吧..
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        return businessMapper.getBusinessById(businessDTO.getId());
    }

    @Override
    public List<BusinessVO> getBusinesses() {
        List<BusinessVO> businesses = businessMapper.getBusinesses();
//        if (businesses == null) {
//            throw new APIException(ResultCodeEnum.NOT_FOUND);
//        }
        return businesses;
    }

    //搜索与筛选商铺信息
    @Override
    public List<BusinessSearchVO> getBusinessesBySearch(String keyword, boolean isScore , boolean isSales) {
        List<BusinessSearchVO> businesses = businessMapper.searchBusinesses(keyword);
//        System.out.println(businesses);
        // 为每个店铺计算评分与销量
        for (BusinessSearchVO business : businesses) {
            Map<String, Object> interactionCounts = businessMapper.getInteractionCounts(business.getId());
            int salesCount = orderClient.orderCount(business.getId()).getData();
            Integer likeCount = interactionMapper.countLikesByMerchantId(business.getId());
            Integer collectCount = interactionMapper.countCollectionsByMerchantId(business.getId());
            // 计算评分 (点赞权重0.6，收藏权重0.4，归一化到1-5分)
            double normalizedRating = 1 + 4 * (0.6 * likeCount / (likeCount + 10.0) + 0.4 * collectCount / (collectCount + 10.0));
            BigDecimal rating = BigDecimal.valueOf(normalizedRating).setScale(2, RoundingMode.HALF_UP);
            business.setScore(rating);
//            System.out.println("Business ID: " + business.getId() +
//                    ", likeCount: " + likeCount +
//                    ", collectCount: " + collectCount +
//                    ", rawRating: " + normalizedRating);
            business.setSalesCount(salesCount);
        }

        // 使用 Comparator 进行排序
        Comparator<BusinessSearchVO> comparator = null;

        if (isScore && isSales) {
            // 先按评分降序，再按销量降序
            comparator = Comparator.comparing(BusinessSearchVO::getScore, Comparator.reverseOrder())
                    .thenComparing(BusinessSearchVO::getSalesCount, Comparator.reverseOrder());
        } else if (isScore) {
            // 按评分降序
            comparator = Comparator.comparing(BusinessSearchVO::getScore, Comparator.reverseOrder());
        } else if (isSales) {
            // 按销量降序
            comparator = Comparator.comparing(BusinessSearchVO::getSalesCount, Comparator.reverseOrder());
        }

        if (comparator != null) {
            businesses.sort(comparator);
        }
//        System.out.println(businesses);
        return businesses;
    }

    //搜索与筛选商铺信息
    @Override
    public List<BusinessSearchVO> getBusinessesInCarousel() {
        List<BusinessSearchVO> businesses = businessMapper.searchBusinesses(null);
        // 为每个店铺计算评分与销量
        for (BusinessSearchVO business : businesses) {
            Map<String, Object> interactionCounts = businessMapper.getInteractionCounts(business.getId());
            int salesCount = orderClient.orderCount(business.getId()).getData();
            Integer likeCount = interactionMapper.countLikesByMerchantId(business.getId());
            Integer collectCount = interactionMapper.countCollectionsByMerchantId(business.getId());
            // 计算评分 (点赞权重0.6，收藏权重0.4，归一化到1-5分)
            double normalizedRating = 1 + 4 * (0.6 * likeCount / (likeCount + 10.0) + 0.4 * collectCount / (collectCount + 10.0));
            BigDecimal rating = BigDecimal.valueOf(normalizedRating).setScale(2, RoundingMode.HALF_UP);
            business.setScore(rating);
            business.setSalesCount(salesCount);
        }

        // 使用 Comparator 进行排序
        Comparator<BusinessSearchVO> comparator = null;
        comparator = Comparator.comparing(BusinessSearchVO::getScore, Comparator.reverseOrder())
                .thenComparing(BusinessSearchVO::getSalesCount, Comparator.reverseOrder());

        businesses.sort(comparator);


        return businesses.subList(0, 3);
    }


    @Override
    public Integer applyForAddBusiness(Business business) {
        User currentUser = userClient.getUserByName(UserContext.getUsername()).getData();

        // 添加 null 检查
        if (currentUser == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }

        // 权限判断 - 检查用户是否有 BUSINESS 或 ADMIN 权限
        boolean hasBusinessPermission = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "BUSINESS".equals(auth.getName()));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getName()));

        // 如果没有 BUSINESS 权限且不是 ADMIN，则抛出权限异常
        if (!hasBusinessPermission && !isAdmin) {
            throw new APIException(ResultCodeEnum.NOT_ENOUGH_PERMISSION);
        }

        // 设置基础信息
        business.setCreator(currentUser.getId());
        business.setCreateTime(LocalDateTime.now());

        // 状态设置：管理员直接通过，普通商家需要审核
        business.setStatus(isAdmin ? 1 : 0);

        // 用户ID设置：管理员创建则必须传入userID，普通商家使用当前用户ID
        if (isAdmin) {
            // 管理员操作，必须传入userId
            if (business.getUserId() == null) {
                throw new APIException(ResultCodeEnum.USER_VALUE_MISSED);// 用户ID不能为空
            }
        } else {
            // 普通商家操作，使用当前用户ID
            business.setUserId(currentUser.getId());
        }
        // 设置默认值
        if (business.getIs_deleted() == null) {
            business.setIs_deleted(false);
        }
        if (business.getDeliveryPrice() == null) {
            business.setDeliveryPrice(BigDecimal.ZERO);
        }
        if (business.getStartPrice() == null) {
            business.setStartPrice(BigDecimal.ZERO);
        }

        return businessMapper.applyForAddBusiness(business);
    }


    @Override
    public List<Business> getMerchantBusinesses(Long userId, Integer status) {
        return businessMapper.selectByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Business> listBusinessByOrderTypeId(Integer type) {
        return businessMapper.listBusinessByOrderTypeId(type);
    }

    @Override
    public List<MerchantStatsVO> getBusinessIdList() {
        User currentUser = userClient.getUserByName(UserContext.getUsername()).getData();

        return businessMapper.selectBusinessIdListByUserId(currentUser.getId());
    }

    @Override
    public BusinessVO patchBusinessOwn(Long id, BusinessUpdateDTO updateDto) {
        User currentUser = userClient.getUserByName(UserContext.getUsername()).getData();

        // 添加 null 检查
        if (currentUser == null) {
            throw new RuntimeException("无法获取当前用户信息");
        }

        // 权限判断 - 检查用户是否有 BUSINESS 或 ADMIN 权限
        boolean hasBusinessPermission = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "BUSINESS".equals(auth.getName()));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getName()));

        // 如果没有 BUSINESS 权限且不是 ADMIN，则抛出权限异常
        if (!hasBusinessPermission && !isAdmin) {
            throw new RuntimeException("权限不足，需要“商家”或“管理员”权限");
        }
        int result = businessMapper.updateBusiness(id, updateDto);
        if (result == 0) {
            throw new RuntimeException("更新商户信息失败，商户不存在或已被删除");
        }

        //判断是不是自己操作自己的店铺或者管理员
        boolean isSelf=isIdPresent(businessMapper.getBusinessIdsByUserId(currentUser.getId()),updateDto.getId());

        if(!isSelf&&!isAdmin){
            throw new RuntimeException("不是该商家自己的商铺，更新失败");
        }
        // 2. 如果有商户所有者信息，更新商户所有者
        if (updateDto.getBusinessOwner() != null) {
            businessMapper.updateBusinessOwner(id, updateDto);
        }
        return businessMapper.getBusinessById(id);
    }

    @Override
    public Business getBusinessInfo(Long id) {
        return businessMapper.selectBusinessById(id);
    }

    @Override
    public List<Business> getBusinessInfoList(Set<Long> businessIds) {
        return businessMapper.selectBusinessByIds(businessIds);
    }



    /**
     * 顾客申请开店
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessPermissionVO applyShop(BusinessPermissionDTO businessPermissionDTO) {

        User currentUser = getCurrentUser();
        Long currentUserId = currentUser.getId();

        if (currentUser == null) {
            throw new APIException("当前用户不存在");
        }
        businessPermissionDTO.setStatus(0);
        businessPermissionDTO.setUserId(currentUserId);
        businessPermissionDTO.setCreator(currentUserId);
        businessPermissionDTO.setUpdater(currentUserId);
        businessPermissionDTO.setCreateTime(LocalDateTime.now());
        businessPermissionDTO.setUpdateTime(LocalDateTime.now());
        businessMapper.insertBusinessPermission(businessPermissionDTO);
        try {
            sendShopApplyNotification(currentUserId, currentUser.getUsername());
        } catch (JSONException e) {
            throw new APIException("消息发送失败");
        }
        BusinessPermissionVO businessPermissionVO = new BusinessPermissionVO();
        BeanUtils.copyProperties(businessPermissionDTO, businessPermissionVO);
        return businessPermissionVO;
    }

    @Override
    public BusinessPermissionVO auditShopApplication(BusinessPermissionDTO businessPermissionDTO) {
        Long currentUserId = getCurrentUser().getId();
        businessPermissionDTO.setUpdater(currentUserId);
        businessPermissionDTO.setUpdateTime(LocalDateTime.now());
        if (businessMapper.selectBusinessById(businessPermissionDTO.getId()) == null) {
            throw new APIException("申请记录不存在");
        }
        businessMapper.updateBusinessStatus(businessPermissionDTO);
        BusinessPermissionVO businessPermissionVO =businessMapper.getBusinessPermissionById(businessPermissionDTO.getId());
        Long applicantUserId = businessPermissionVO.getUserId();
        if (businessPermissionDTO.getStatus() == 1) { // 1-同意
            sendAuditPassNotification(applicantUserId,1);
        } else {
            // 若拒绝，可选择性推送拒绝通知
            sendAuditRejectNotification(applicantUserId,1);
        }
        return businessPermissionVO;
    }

    @Override
    public List<BusinessPermissionVO> getShopApplications() {
        List<BusinessPermissionVO> applications =businessMapper.listNotAudited();
        return applications;
    }


    /**
     * 获取当前用户ID
     */
    private User getCurrentUser() {
        return userClient.getUserByName(UserContext.getUsername()).getData();
    }

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 向管理员推送开店申请通知
     * @param userId 申请人ID
     * @param username 申请人用户名
     */
    private void sendShopApplyNotification(Long userId, String username) throws JSONException {
        // 构建消息体（包含type、userId、content）
        JSONObject message = new JSONObject();
        message.put("currentTime", LocalDateTime.now().format(TIME_FORMATTER));
        message.put("type", 1); // 1表示申请开店
        message.put("userId", userId);
        message.put("content", "商家[" + username + "]申请开店，请及时审核");

        // 调用WebSocket服务群发消息（管理员客户端会监听该消息）
        notificationClient.pushMessage(new WebSocketPushDTO(null,message.toJSONString()));
    }

    /**
     * 推送"审核通过"通知给顾客
     */
    private void sendAuditPassNotification(Long userId,Integer type) {
        JSONObject message = new JSONObject();
        message.put("currentTime", LocalDateTime.now().format(TIME_FORMATTER));

        NotificationSendDTO notification = new NotificationSendDTO();

        if(type==0){
            String content = "恭喜！您的成为商家申请已通过审核，现在可以开始营业了";
            notification.setText(content);
            message.put("type", 0); // 0表示申请成为商家的回复
            message.put("content",content);
        }else if(type==1){
            String content = "恭喜！您的开店申请已通过审核，现在可以开始营业了";
            notification.setText(content);
            message.put("type", 1); // 1表示申请开店的回复
            message.put("content", content);
        }
        message.put("userId", userId);
        notificationClient.pushMessage(new WebSocketPushDTO(userId,message.toJSONString()));

        notification.setReceiverId(userId); // 接收消息的用户ID
        notification.setType(type); // 0=商家申请，1=开店申请
        notification.setAuditResult(String.valueOf(1)); // 1=通过，2=拒绝
        notificationClient.sendNotification(notification);
    }

    /**
     * 推送"审核拒绝"通知给顾客
     */
    private void sendAuditRejectNotification(Long userId,Integer type) {
        JSONObject message = new JSONObject();
        message.put("currentTime", LocalDateTime.now().format(TIME_FORMATTER));
        NotificationSendDTO notification = new NotificationSendDTO();
        if(type==0){
            String content = "抱歉，您的成为商家申请未通过审核";
            notification.setText(content);
            message.put("type", 0); // 0表示申请成为商家的回复
            message.put("content", content);
        }else if(type==1){
            String content = "抱歉，您的开店申请未通过审核";
            notification.setText(content);
            message.put("type", 1); // 1表示申请开店的回复
            message.put("content", content);
        }
        message.put("userId", userId);

        notificationClient.pushMessage(new WebSocketPushDTO(userId,message.toJSONString()));

        notification.setReceiverId(userId); // 接收消息的用户ID
        notification.setType(type); // 0=商家申请，1=开店申请
        notification.setAuditResult(String.valueOf(2)); // 1=通过，2=拒绝
        notificationClient.sendNotification(notification);
    }

}
