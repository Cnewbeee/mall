import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
	state: {
		hasLogin: false,
		userInfo: {},
	},
	mutations: {
		login(state, provider) {

			state.hasLogin = true;
			state.userInfo = provider;
			uni.setStorage({//缓存用户登陆状态
			    key: 'userInfo',  
			    data: provider  
			}) 
			console.log(state.userInfo);
		},
		logout(state) {
			state.hasLogin = false;
			state.userInfo = {};
			uni.removeStorage({  
				key: 'userInfo'  
			});
			uni.removeStorage({
				key: 'token'  
			})
		},
		// 新增：支持局部更新用户信息
		UPDATE_USER_INFO(state, payload) {
			state.userInfo = Object.assign({}, state.userInfo, payload);
			uni.setStorage({
				key: 'userInfo',
				data: state.userInfo
			});
		}
	},
	actions: {
	
	}
})

export default store
