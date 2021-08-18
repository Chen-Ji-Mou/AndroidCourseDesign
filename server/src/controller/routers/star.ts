import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { CTX } from '../type';

export default function addStarRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 点赞一个动态（取消的话再传一次就好）
         */
        .post("/api/star", handler(
            async (ctx) => {
                // todo
            })
        )

        /**
         * 查看一个动态下的点赞数
         */
        .get("/api/star/count", handler(
            async (ctx) => {
                // todo
            })
        )
}