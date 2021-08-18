import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { CTX } from '../type';

export default function addCommentRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 增加一个评论
         */
        .post("/api/comment", handler(
            async (ctx) => {
                // todo
            })
        )

        /**
         * 查看一个动态下的所有评论
         */
        .get("/api/comment/list", handler(
            async (ctx) => {
                // todo
            })
        )

        /** 
         * 删除某条评论
         */
        .delete("/api/comment", handler(
            async (ctx) => {
                // todo
            })
        )
}
