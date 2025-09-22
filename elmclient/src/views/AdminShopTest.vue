<template>
	<div class="wrapper">
		<header class="topbar"><p>商铺管理 - {{ businessName || '商家' }}</p></header>
		<div class="content">
			<!-- <div class="toolbar">
				<button class="back" @click="goBack">返回</button>
			</div> -->
			<ul class="store-list">
				<li v-for="s in storeList" :key="s.businessId" class="store-item">
					<div class="store-info">
						<img :src="s.businessImg || defaultImg" class="logo" @error="onImgError" />
						<div class="meta">
							<p class="name">{{ s.businessName }}</p>
							<p class="addr">{{ s.businessAddress }}</p>
							<p class="desc">{{ s.businessDesc || '暂无简介' }}</p>
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
						<textarea v-model="editor.form.businessDesc" placeholder="请输入商铺简介"></textarea>
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

export default {
	name: 'ManageShop',
	setup() {
		const route = useRoute();
		const router = useRouter();
		const ownerId = ref(null);
		const businessName = ref('');
		const storeList = ref([]);
		const defaultImg = '/R-C.png';

		// 硬编码的商铺数据
		const mockStores = [
			{ 
				businessId: '1001-A', 
				businessName: '美味小厨（一店）', 
				businessImg: '', 
				businessAddress: '天津市和平区南京路188号',
				businessDesc: '主营川湘菜系，特色菜有麻辣香锅、水煮鱼等'
			},
			{ 
				businessId: '1001-B', 
				businessName: '美味小厨（二店）', 
				businessImg: '', 
				businessAddress: '天津市河西区围堤道88号',
				businessDesc: '分店，环境优雅，提供包间服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			},
			{ 
				businessId: '1001-C', 
				businessName: '美味小厨（三店）', 
				businessImg: '', 
				businessAddress: '天津市南开区卫津路66号',
				businessDesc: '新开分店，主打快餐和外卖服务'
			}
		];

		const editor = reactive({
			visible: false,
			mode: 'edit',
			form: { 
				businessId: null, 
				businessName: '', 
				businessImg: '', 
				businessAddress: '',
				businessDesc: '' 
			}
		});

		const goBack = () => router.back();
		const onImgError = (e) => { e.target.src = defaultImg; };

		const loadStores = () => {
			storeList.value = mockStores;
		};

		const startEdit = (store) => { 
			editor.mode = 'edit'; 
			editor.form = { ...store }; 
			editor.visible = true; 
		};
		
		const startCreate = () => { 
			editor.mode = 'create'; 
			editor.form = { 
				businessId: null, 
				businessName: '', 
				businessImg: '', 
				businessAddress: '',
				businessDesc: '' 
			}; 
			editor.visible = true; 
		};
		
		const closeEditor = () => { editor.visible = false; };

		const saveStore = () => {
			if (editor.mode === 'create') {
				// 生成新的商铺ID
				const newId = `${ownerId.value}-${String.fromCharCode(65 + storeList.value.length)}`;
				storeList.value.push({ 
					...editor.form, 
					businessId: newId 
				});
			} else {
				const idx = storeList.value.findIndex(s => s.businessId === editor.form.businessId);
				if (idx >= 0) storeList.value[idx] = { ...storeList.value[idx], ...editor.form };
			}
			editor.visible = false;
		};

		const removeStore = (store) => {
			if (!confirm('确认删除该商铺吗？')) return;
			storeList.value = storeList.value.filter(s => s.businessId !== store.businessId);
		};

		onMounted(() => {
			ownerId.value = route.query.ownerId ? String(route.query.ownerId) : '1001';
			businessName.value = route.query.merchantName || '美味小厨';
			loadStores();
		});

		return { 
			businessName, 
			storeList, 
			defaultImg, 
			onImgError, 
			goBack, 
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
.content { margin-top: 12vw; padding: 4vw; }
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
.store-list { list-style: none; padding-bottom: 40vw; margin: 0; }
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