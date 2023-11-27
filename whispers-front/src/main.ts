import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import ToastPlugin from 'vue-toast-notification';
import 'vue-toast-notification/dist/theme-default.css';
import './style.css';
import App from './App.vue';
import Home from './components/Home.vue';
import Trending from './components/Trending.vue';
import MyWhispers from './components/MyWhispers.vue';
import Login from './components/Login.vue';
import Feed from './components/Feed.vue';
import Topic from './components/Topic.vue';
import { startFakeApiServer } from './server';

if (!import.meta.env.VITE_API_BASE) {
  startFakeApiServer();
}

const routes = [
    { path: '/login', component: Login },
    {
        path: '/',
        component: Home,
        children: [
            { path: '/', component: Feed },
            { path: '/trending', component: Trending },
            { path: '/my-whispers', component: MyWhispers },
            { path: '/topics/:topic', component: Topic },
        ]
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to) => {
    const isAuthenticated = localStorage.getItem('username') !== null;
    if (!isAuthenticated && to.path !== '/login') {
        return { path: 'login' };
    }
});

const app = createApp(App);
app.use(router);
app.use(ToastPlugin, {
  position: 'top-right',
});
app.mount('#app');
