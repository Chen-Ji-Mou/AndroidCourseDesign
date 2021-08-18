import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { CTX } from '../type';

export default function addUserRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 注册
         */
        .post("/api/user/register", handler(
            async (ctx) => {
                // todo
            })
        )
        /**
         * 登陆
         */
        .post("/api/user/login", handler(
            async (ctx) => {
                // todo
            })
        )
}

