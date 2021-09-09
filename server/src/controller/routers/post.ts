import Router from "koa-router";
import { MySQL } from "../../database";
import { toHumpFieldObject } from "../../utlis";
import { handler, verifyToken } from "../helper";
import { Post, User } from "../model";
import { ResultUtil } from 'ningx';
import { DeletePostRequestData, GetAPostRequestData, GetUserPostRequestDate as getUserPostRequestData, NewPostRequestData } from "../type";
import { ApiError } from "../../model";
import { v4 as uuid } from 'uuid';

export default function addPostRouter(router: Router<any, {}>, database: MySQL) {
    router
        /** 
         * 获取首页动态列表
         */
        .get("/api/post/hello", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                let [searchPostList] = await database.execute(
                    `SELECT post.id, post.content, post.user_id, post.pictures, post.date, IF(star.user_id, TRUE, FALSE) as is_star FROM post LEFT JOIN star ON post.id=star.post_id ORDER BY date;`
                ) as [Array<any>, any];
                const postList: Array<Post> = searchPostList.map(x => {
                    return {
                        ...toHumpFieldObject(x),
                        pictures: x.pictures.split(",")
                    };
                });

                return ResultUtil.success(postList);
            })
        )

        /** 
         * 获取某个人的动态
         */
        .get("/api/post/profile", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: getUserPostRequestData = ctx.query as any;
                if (!requestData.userId) throw new ApiError("目标用户id不能为空");

                const [searchUser] = await database.execute(
                    `SELECT * FROM user WHERE id='${requestData.userId}'`
                ) as [Array<User>, any];
                if (searchUser.length === 0) throw new ApiError("目标用户不存在");

                const [searchPostList] = await database.execute(
                    `SELECT * FROM post WHERE user_id='${requestData.userId}' ORDER BY date;`
                ) as [Array<any>, any];
                const postList: Array<Post> = searchPostList.map(x => {
                    return {
                        ...toHumpFieldObject(x),
                        pictures: x.pictures.split(",")
                    };
                });

                return ResultUtil.success(postList);
            })
        )

        /** 
         * 发布新动态
         */
        .post("/api/post", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: NewPostRequestData = ctx.request.body;
                if (!requestData.content) throw new ApiError("内容不能为空");
                if (!requestData.pictures || requestData.pictures.length < 1) throw new ApiError("至少上传一张图片");

                const newPost: Post = { id: uuid(), userId: tokenData.id, ...requestData, date: (new Date().getTime()) };
                await database.execute(
                    `INSERT INTO post (id, content, user_id, pictures, date) VALUES('${newPost.id}', '${newPost.content}', '${newPost.userId}', '${newPost.pictures}', '${newPost.date}');`
                );

                return ResultUtil.success({ id: newPost.id });
            })
        )

        /** 
         * 删除某条动态
         */
        .post("/api/post/delete", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: DeletePostRequestData = ctx.request.body;
                if (!requestData.id) throw new ApiError("删除目标id不能为空");

                const [searchPost] = await database.execute(
                    `SELECT * FROM post WHERE id='${requestData.id}'`
                ) as [Array<any>, any];
                if (searchPost.length === 0) throw new ApiError("未找到对应动态");
                const targetPost: Post = toHumpFieldObject(searchPost[0]);

                if (targetPost.userId !== tokenData.id) throw new ApiError("不能删除别人的动态");

                await database.execute(
                    `DELETE FROM post WHERE id='${requestData.id}'`
                );
                return ResultUtil.success(null);
            })
        )

        /** 
         * 获取某条动态
         */
        .get("/api/post/:id", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: GetAPostRequestData = ctx.params as any;
                if (!requestData.id) throw new ApiError("目标id不能为空");

                const [searchPost] = await database.execute(
                    `SELECT post.id, post.content, post.user_id, post.pictures, post.date, IF(star.user_id, TRUE, FALSE) as is_star FROM post LEFT JOIN star ON post.id=star.post_id AND star.user_id='${tokenData.id}' WHERE post.id='${requestData.id}' ORDER BY date;`
                ) as [Array<any>, any];
                if (searchPost.length === 0) throw new ApiError("目标动态不存在");
                
                const targetPost : Post = {
                    ...toHumpFieldObject(searchPost[0]),
                    pictures: searchPost[0].pictures.split(",")
                };

                return ResultUtil.success(targetPost);
            })
        )
}
