<template>
	<div class="wrapper">
		<header class="topbar"><p>商铺管理 - {{ businessName || '商家' }}</p></header>
		<div class="content">
			<ul class="store-list">
				<li v-for="s in storeList" :key="s.id" class="store-item">
					<div class="store-info">
						<img :src="s.businessImg || defaultImg" class="logo" @error="onImgError" />
						<div class="meta">
							<p class="name">{{ s.businessName }}</p>
							<p class="addr">{{ s.businessAddress }}</p>
							<p class="desc">{{ s.businessExplain || '暂无简介' }}</p>
						</div>
					</div>
					<div class="actions">
						<button class="edit" @click="startEdit(s)">编辑</button>
						<button class="del" @click="removeStore(s)">删除</button>
					</div>
				</li>
			</ul>

			<!-- 底部新增按钮 -->
			<div class="bottom-bar">
				<button class="add" @click="startCreate">新增商铺</button>
			</div>

			<!-- 编辑/新增弹出层 -->
			<div v-if="editor.visible" class="editor">
				<div class="card">
					<h3>{{ editor.mode === 'create' ? '新增商铺' : '编辑商铺' }}</h3>
					<div class="form">
						<label>商铺名称 <span class="limit">(不超过10字)</span></label>
						<input 
							v-model="editor.form.businessName" 
							placeholder="请输入商铺名称" 
							@input="validateName"
						/>
						<p class="error" v-if="nameError">{{ nameError }}</p>
						
						<label>商铺图片</label>
						<div class="upload-area">
							<input 
								type="file" 
								ref="fileInput" 
								@change="handleFileChange" 
								accept="image/*" 
								style="display: none"
							/>
							<button class="upload-btn" @click="triggerFileInput">选择图片</button>
							<span class="file-name">{{ uploadFileName || '未选择文件' }}</span>
							<button 
								class="upload-submit" 
								@click="uploadImage" 
								:disabled="!selectedFile"
							>
								上传
							</button>
						</div>
						<div class="image-preview" v-if="editor.form.businessImg">
							<img :src="editor.form.businessImg" alt="预览" />
						</div>
						
						<label>商铺地址 <span class="limit">(不超过20字)</span></label>
						<input 
							v-model="editor.form.businessAddress" 
							placeholder="请输入地址" 
							@input="validateAddress"
						/>
						<p class="error" v-if="addressError">{{ addressError }}</p>
						
						<label>商铺简介 <span class="limit">(不超过30字)</span></label>
						<textarea 
							v-model="editor.form.businessExplain" 
							placeholder="请输入商铺简介"
							@input="validateExplain"
						></textarea>
						<p class="error" v-if="explainError">{{ explainError }}</p>
					</div>
					<div class="editor-actions">
						<button class="cancel" @click="closeEditor">取消</button>
						<button class="save" @click="saveStore">保存</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

export default {
	name: 'ManageShop',
	setup() {
		const route = useRoute();
		const router = useRouter();
		const ownerId = ref(null);
		const businessName = ref('');
		const storeList = ref([]);
		const defaultImg = '/R-C.png';
		const fileInput = ref(null);
		const selectedFile = ref(null);
		const uploadFileName = ref('');

		// 验证错误信息
		const nameError = ref('');
		const addressError = ref('');
		const explainError = ref('');

		const editor = reactive({
			visible: false,
			mode: 'edit',
			form: { 
				id: null,
				businessName: '', 
				businessImg: '', 
				businessAddress: '',
				businessExplain: '' 
			}
		});

		const onImgError = (e) => { e.target.src = defaultImg; };

		const loadStores = async () => {
			try {
				const response = await request.get('/api/businesses/merchant', {
					params: {
						userId: ownerId.value,
						status: 1
					}
				});
				if (response.success) {
					storeList.value = response.data || [];	
				}
			} catch (error) {
				console.error('获取商铺列表失败:', error);
				// 备用数据
				storeList.value = [
					{ 
						id: 1,
						businessName: '虾滑不WA火锅', 
						businessAddress: '天津市和平区', 
						businessExplain: '不限量AC虾滑',
						businessImg: 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/c55e6a1a-17fd-4661-9f1b-722610e5cf1c.png'
					},
					{ 
						id: 2,
						businessName: '黄焖鸡米饭', 
						businessAddress: '梅园二楼', 
						businessExplain: '好吃',
						businessImg: null
					}
				];
			}
		};

		// 验证商铺名称
		const validateName = () => {
			if (editor.form.businessName.length > 10) {
				nameError.value = '商铺名称不能超过10个字';
			} else {
				nameError.value = '';
			}
		};

		// 验证商铺地址
		const validateAddress = () => {
			if (editor.form.businessAddress.length > 20) {
				addressError.value = '商铺地址不能超过20个字';
			} else {
				addressError.value = '';
			}
		};

		// 验证商铺简介
		const validateExplain = () => {
			if (editor.form.businessExplain.length > 30) {
				explainError.value = '商铺简介不能超过30个字';
			} else {
				explainError.value = '';
			}
		};

		// 验证所有字段
		const validateAll = () => {
			validateName();
			validateAddress();
			validateExplain();
			return !nameError.value && !addressError.value && !explainError.value;
		};

		const triggerFileInput = () => {
			fileInput.value.click();
		};

		const handleFileChange = (e) => {
			const file = e.target.files[0];
			if (file) {
				selectedFile.value = file;
				uploadFileName.value = file.name;
			}
		};

		const uploadImage = async () => {
			if (!selectedFile.value) return;

			try {
				const formData = new FormData();
				formData.append('file', selectedFile.value);

				const response = await request.post('/upload', formData, {
					headers: {
						'Content-Type': 'multipart/form-data'
					}
				});

				if (response.success) {
					editor.form.businessImg = response.data;
					selectedFile.value = null;
					uploadFileName.value = '上传成功';
					// ElMessage.success('图片上传成功');
				} else {
					console.error('上传失败:', response.message);
					uploadFileName.value = '上传失败';
					// ElMessage.error('图片上传失败');
				}
			} catch (error) {
				console.error('上传图片出错:', error);
				uploadFileName.value = '上传出错';
				// ElMessage.error('图片上传出错');
			}
		};

		const startEdit = (store) => { 
			editor.mode = 'edit'; 
			editor.form = { ...store }; 
			editor.visible = true;
			selectedFile.value = null;
			uploadFileName.value = '';
			// 重置错误信息
			nameError.value = '';
			addressError.value = '';
			explainError.value = '';
		};
		
		const startCreate = () => { 
			editor.mode = 'create'; 
			editor.form = { 
				id: null,
				businessName: '', 
				businessImg: '', 
				businessAddress: '',
				businessExplain: '' 
			}; 
			editor.visible = true;
			selectedFile.value = null;
			uploadFileName.value = '';
			// 重置错误信息
			nameError.value = '';
			addressError.value = '';
			explainError.value = '';
		};
		
		const closeEditor = () => { 
			editor.visible = false;
			selectedFile.value = null;
			uploadFileName.value = '';
			// 重置错误信息
			nameError.value = '';
			addressError.value = '';
			explainError.value = '';
		};

		const saveStore = async () => {
  // 先验证所有字段
  if (!validateAll()) {
    // ElMessage.warning('请检查输入内容是否符合要求');
    return;
  }

  try {
    if (editor.mode === 'create') {
      // 构建符合Business实体类的请求体
      const businessData = {
        businessName: editor.form.businessName,
        businessAddress: editor.form.businessAddress,
        businessExplain: editor.form.businessExplain,
        businessImg: editor.form.businessImg,
        orderTypeId: 1, // 默认值设为1
        userId: ownerId.value, // 必传字段
       // creator: ownerId.value, // 创建人ID
      };

      const response = await request.post('/api/businesses/apply', businessData);
      
      if (response.success) {
        // 确保response.data包含所有必要字段
        const newStore = {
          id: response.data.id || Date.now(), // 确保有id字段
          businessName: response.data.businessName || editor.form.businessName,
          businessAddress: response.data.businessAddress || editor.form.businessAddress,
          businessExplain: response.data.businessExplain || editor.form.businessExplain,
          businessImg: response.data.businessImg || editor.form.businessImg,
          // 其他必要字段...
        };
        
        // 将新商铺添加到列表最前面
        storeList.value.unshift(newStore);
        
      } else {
        // ElMessage.error(response.message || '商铺申请失败');
      }
    } else {
      // 编辑逻辑保持不变
      await request.put(`/api/businesses/${editor.form.id}`, editor.form);
      const idx = storeList.value.findIndex(s => s.id === editor.form.id);
      if (idx >= 0) storeList.value[idx] = { ...storeList.value[idx], ...editor.form };
    //   ElMessage.success('商铺更新成功');
    }
    editor.visible = false;
  } catch (error) {
    console.error('保存商铺失败:', error);
    if (editor.mode === 'create') {
      // 本地模拟时也确保所有字段都有值
      storeList.value.unshift({ 
        id: Date.now(),
        businessName: editor.form.businessName,
        businessAddress: editor.form.businessAddress,
        businessExplain: editor.form.businessExplain,
        businessImg: editor.form.businessImg,
        orderTypeId: 1,
        userId: ownerId.value
      });
    //   ElMessage.success('商铺添加成功(本地模拟)');
    } else {
      const idx = storeList.value.findIndex(s => s.id === editor.form.id);
      if (idx >= 0) storeList.value[idx] = { ...storeList.value[idx], ...editor.form };
    //   ElMessage.success('商铺更新成功(本地模拟)');
    }
    editor.visible = false;
  }
};

		const removeStore = async (store) => {
			if (!confirm('确认删除该商铺吗？')) return;
			try {
				await request.delete(`/api/businesses/${store.id}`);
				storeList.value = storeList.value.filter(s => s.id !== store.id);
				// ElMessage.success('商铺删除成功');
			} catch (error) {
				console.error('删除商铺失败:', error);
				storeList.value = storeList.value.filter(s => s.id !== store.id);
				// ElMessage.success('商铺删除成功');
			}
		};

		onMounted(() => {
			ownerId.value = route.query.ownerId;
			businessName.value = route.query.merchantName || '';
			loadStores();
		});

		return { 
			businessName, 
			storeList, 
			defaultImg, 
			onImgError, 
			startEdit, 
			startCreate, 
			closeEditor, 
			saveStore, 
			removeStore, 
			editor,
			fileInput,
			selectedFile,
			uploadFileName,
			nameError,
			addressError,
			explainError,
			triggerFileInput,
			handleFileChange,
			uploadImage,
			validateName,
			validateAddress,
			validateExplain
		};
	}
};
</script>

<style scoped>
/* 原有样式保持不变 */
.wrapper { width: 100%; min-height: 100vh; background: #fff; }
.topbar { 
	width: 100%; 
	height: 12vw; 
	background: #409eff; 
	color: #fff; 
	font-size: 4.8vw; 
	position: fixed; 
	left: 0; 
	top: 0; 
	z-index: 1000; 
	display: flex; 
	justify-content: center; 
	align-items: center; 
}
.content { margin-top: 15vw; padding: 4vw; }
.toolbar { display: flex; gap: 2vw; margin-bottom: 2vw; }
.back { 
	background: #eee; 
	color: #333; 
	border: none; 
	border-radius: 1.2vw; 
	padding: 1.6vw 3vw; 
	font-size: 3.6vw; 
}
.add { 
	background: #1e80ff; 
	color: #fff; 
	border: none; 
	border-radius: 1.2vw; 
	padding: 1.6vw 3vw; 
	font-size: 3.6vw; 
}
.store-list { list-style: none; padding-bottom: 30vw; margin: 0; }
.store-item { 
	display: flex; 
	align-items: center; 
	justify-content: space-between; 
	padding: 3vw; 
	border-bottom: 1px solid #f0f0f0; 
}
.store-info {
	display: flex;
	align-items: center;
	flex: 1;
}
.logo { 
	width: 20vw; 
	height: 20vw; 
	object-fit: cover; 
	border-radius: 1vw; 
	margin-right: 3vw; 
}
.meta { 
	display: flex; 
	flex-direction: column; 
	flex: 1;
}
.name { 
	font-size: 4vw; 
	color: #333; 
	margin-bottom: 1vw;
}
.addr { 
	font-size: 3.2vw; 
	color: #777; 
	margin-bottom: 1vw;
}
.desc {
	font-size: 3.2vw;
	color: #666;
	line-height: 1.4;
}
.actions { 
	display: flex; 
	flex-direction: column; 
	gap: 2vw; 
	align-items: center; 
}
.edit { 
	background: #1e80ff; 
	color: #fff; 
	border: none; 
	border-radius: 1.2vw; 
	padding: 1.6vw 3vw; 
	font-size: 3.6vw; 
}
.del { 
	background: #fff; 
	color: #e15656; 
	border: 1px solid #f3caca; 
	border-radius: 1.2vw; 
	padding: 1.6vw 3vw; 
	font-size: 3.6vw; 
}
.editor { 
	position: fixed; 
	inset: 0; 
	background: rgba(0,0,0,.35); 
	display: flex; 
	align-items: center; 
	justify-content: center; 
	z-index: 1001;
}
.editor .card { 
	width: 86vw; 
	background: #fff; 
	border-radius: 1.6vw; 
	padding: 4vw; 
	max-height: 80vh;
	overflow-y: auto;
}
.editor .form { 
	display: flex; 
	flex-direction: column; 
	gap: 2vw; 
	margin: 2vw 0; 
}
.editor .form label { 
	font-size: 3.2vw; 
	color: #555; 
}
.editor .form input, 
.editor .form textarea { 
	height: 9vw; 
	font-size: 3.6vw; 
	padding: 0 2vw; 
	border: 1px solid #eee; 
	border-radius: 1vw; 
}
.editor .form textarea {
	height: 18vw;
	padding: 2vw;
	resize: vertical;
}
.editor-actions { 
	display: flex; 
	justify-content: flex-end; 
	gap: 2vw; 
}
.editor-actions .cancel { 
	background: #eee; 
	color: #333; 
	border: none; 
	border-radius: 1.2vw; 
	padding: 1.6vw 3vw; 
	font-size: 3.6vw; 
}
.editor-actions .save { 
	background: #1e80ff; 
	color: #fff; 
	border: none; 
	border-radius: 1.2vw; 
	padding: 1.6vw 3vw; 
	font-size: 3.6vw; 
}

.bottom-bar {
  position: fixed;
  right: 4vw;
  bottom: 18vw;
  background: transparent;
}
.bottom-bar .add {
  border-radius: 8vw;
  padding: 2.2vw 5vw;
  box-shadow: 0 6px 16px rgba(30,128,255,.35);
}

/* 新增的上传相关样式 */
.upload-area {
  display: flex;
  align-items: center;
  gap: 2vw;
  margin-bottom: 2vw;
}
.upload-btn {
  background: #409eff;
  color: white;
  border: none;
  border-radius: 1.2vw;
  padding: 1.6vw 3vw;
  font-size: 3.6vw;
  cursor: pointer;
}
.upload-submit {
  background: #67c23a;
  color: white;
  border: none;
  border-radius: 1.2vw;
  padding: 1.6vw 3vw;
  font-size: 3.6vw;
  cursor: pointer;
}
.upload-submit:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}
.file-name {
  font-size: 3.2vw;
  color: #666;
  flex: 1;
}
.image-preview {
  margin-top: 2vw;
}
.image-preview img {
  width: 100%;
  max-height: 30vw;
  object-fit: contain;
  border-radius: 1vw;
  border: 1px solid #eee;
}

/* 新增的验证相关样式 */
.limit {
  color: #999;
  font-size: 2.8vw;
}
.error {
  color: #f56c6c;
  font-size: 3vw;
  margin-top: -1vw;
  margin-bottom: 1vw;
}
</style>