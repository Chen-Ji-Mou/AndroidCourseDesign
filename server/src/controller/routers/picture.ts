import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { UploadPictureRequestData } from '../type';
import { ApiError } from './../../model';
import { v4 as uuid } from 'uuid';
import { ResultUtil } from 'ningx';

export default function addPictureRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 上传一个新图片
         */
        .post("/api/picture", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: UploadPictureRequestData = ctx.request.body;
                if (!requestData.base64) throw new ApiError("未传入图片base64");

                const newPicture = { ...requestData, id: uuid() };
                await database.execute(
                    `INSERT INTO picture (id, base64) VALUES('${newPicture.id}', '${newPicture.base64}');`
                );

                return ResultUtil.success({id: newPicture.id});
            })
        )

        /**
         * 图片地址
         */
        .get("/api/picture/:id", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);
            })
        )
}
