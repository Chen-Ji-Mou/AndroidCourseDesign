import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { CTX } from '../type';

export default function addPictureRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 上传一个新图片
         */
        .post("/api/picture", handler(
            async (ctx: CTX) => {
                // todo
            })
        )

        /**
         * 图片地址
         */
        .get("/api/picture/:id", handler(
            async (ctx: CTX) => {
                // todo
            })
        )
}
