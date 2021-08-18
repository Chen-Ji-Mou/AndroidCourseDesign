import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { GetPictureRequestData, UploadPictureRequestData } from '../type';
import { ApiError } from './../../model';
import { v4 as uuid } from 'uuid';
import { ResultUtil } from 'ningx';
import { Picture } from '../model';

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

                return ResultUtil.success({ id: newPicture.id });
            })
        )

        /**
         * 图片地址
         */
        .get("/api/picture/:id", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: GetPictureRequestData = ctx.params as any;
                if (!requestData.id) throw new ApiError("未传入图片id");

                const [searchPicture] = await database.execute(
                    `SELECT * FROM picture WHERE id='${requestData.id}'`
                ) as [Array<Picture>, any];
                if (searchPicture.length === 0) throw new ApiError("找不到对应图片");

                return ResultUtil.success(searchPicture[0]);
            })
        )
}
