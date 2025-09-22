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
						<label>商铺名称</label>
						<input v-model="editor.form.businessName" placeholder="请输入商铺名称" />
						<label>图片地址</label>
						<input v-model="editor.form.businessImg" placeholder="http(s)://..." />
						<label>商铺地址</label>
						<input v-model="editor.form.businessAddress" placeholder="请输入地址" />
						<label>商铺简介</label>
						<textarea v-model="editor.form.businessExplain" placeholder="请输入商铺简介"></textarea>
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
export default {
	name: 'ManageShop',
	setup() {
		const route = useRoute();
		const router = useRouter();
		const ownerId = ref(null);
		const businessName = ref('');
		const storeList = ref([]);
		const defaultImg = '/R-C.png';

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
				console.log('response响应:', response);
				console.log('responsedata响应:', response.data);
				if (response.success) {
					console.log('success商铺列表:', response.data);
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

		const startEdit = (store) => { 
			editor.mode = 'edit'; 
			editor.form = { ...store }; 
			editor.visible = true; 
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
		};
		
		const closeEditor = () => { editor.visible = false; };

		const saveStore = async () => {
			try {
				if (editor.mode === 'create') {
					// 调用新增接口
					const response = await request.post('/api/businesses', {
						...editor.form,
						userId: ownerId.value
					});
					storeList.value.push(response.data);
				} else {
					// 调用更新接口
					await request.put(`/api/businesses/${editor.form.id}`, editor.form);
					const idx = storeList.value.findIndex(s => s.id === editor.form.id);
					if (idx >= 0) storeList.value[idx] = { ...storeList.value[idx], ...editor.form };
				}
				editor.visible = false;
			} catch (error) {
				console.error('保存商铺失败:', error);
				// 本地模拟保存
				if (editor.mode === 'create') {
					storeList.value.push({ 
						...editor.form, 
						id: Date.now() 
					});
				} else {
					const idx = storeList.value.findIndex(s => s.id === editor.form.id);
					if (idx >= 0) storeList.value[idx] = { ...storeList.value[idx], ...editor.form };
				}
				editor.visible = false;
			}
		};

		const removeStore = async (store) => {
			if (!confirm('确认删除该商铺吗？')) return;
			try {
				await request.delete(`/api/businesses/${store.id}`);
				storeList.value = storeList.value.filter(s => s.id !== store.id);
			} catch (error) {
				console.error('删除商铺失败:', error);
				storeList.value = storeList.value.filter(s => s.id !== store.id);
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
			editor 
		};
	}
};
</script>

<style scoped>
/* 样式保持不变 */
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
</style>