import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { GetPictureRequestData, UploadPictureRequestData } from '../type';
import { ApiError } from './../../model';
import { v4 as uuid } from 'uuid';
import fs from 'fs';
import path from 'path';
import mime from 'mime-types';
import { ResultUtil } from '../../utlis';

export default function addPictureRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 上传一个新图片
         */
        .post("/api/picture", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                if (!ctx.request.files) throw new ApiError("未上传文件");
                const file: any = ctx.request.files.file; // 获取上传文件

                const fileName = uuid() + '.jpg';
                // 创建可读流
                const reader = fs.createReadStream(file.path);
                let filePath = path.join(__dirname, '../../../files/') + `${fileName}`;
                // 创建可写流
                const upStream = fs.createWriteStream(filePath);
                // 可读流通过管道写入可写流
                reader.pipe(upStream);

                return ResultUtil.success({ id: fileName });
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

                let filePath = path.join(__dirname, `../../../files/${requestData.id}`); //图片地址
                let file = null;
                try {
                    file = fs.readFileSync(filePath); //读取文件
                } catch (error) {
                    //如果服务器不存在请求的图片    
                    throw new ApiError("图片不存在");
                }
                let mimeType = mime.lookup(filePath); //读取图片文件类型
                ctx.set('content-type', mimeType); //设置返回类型
                return ResultUtil.self(file);
            })
        )
}
