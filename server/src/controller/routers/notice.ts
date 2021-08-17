import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { CTX } from '../type';

export default function addNoticeRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 获取某个用户的所有通知
         */
        .get("/api/notice/all", handler(
            async (ctx: CTX) => {
                // todo
            })
        )

        /**
         * 某个通知变为已读
         */
        .post("/api/notice/read", handler(
            async (ctx: CTX) => {
                // todo
            })
        )
}
