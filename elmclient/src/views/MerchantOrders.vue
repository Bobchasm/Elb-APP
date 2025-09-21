<template>
    <div class="merchant-orders">
        <h1>商家订单</h1>
        <!-- 订单列表区域 -->
        <ul>
            <li v-for="order in orders" :key="order.id">
                订单号: {{ order.id }}，客户: {{ order.customer }}，金额: {{ order.amount }}
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: 'MerchantOrders',
    data() {
        return {
            orders: [
                { id: '001', customer: '张三', amount: 120 },
                { id: '002', customer: '李四', amount: 230 },
                { id: '003', customer: '王五', amount: 180 }
            ]
        }
    }
}
</script>

<style scoped>
.merchant-orders {
    padding: 20px;
}
</style>