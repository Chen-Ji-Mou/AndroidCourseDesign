import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { NewStarRequestData } from '../type';
import { ApiError } from '../../model';
import { Post, Star } from '../model';
import { v4 as uuid } from 'uuid';
import { ResultUtil, toHumpFieldObject } from 'ningx';
import { notice } from './notice';

export default function addStarRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 点赞一个动态（取消的话再传一次就好）
         */
        .post("/api/star", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: NewStarRequestData = ctx.request.body;
                if (!requestData.postId) throw new ApiError("postId不能为空");

                const [searchPost] = await database.execute(
                    `SELECT * FROM post WHERE id='${requestData.postId}'`
                ) as [Array<any>, any];
                if (searchPost.length === 0) throw new ApiError("目标动态不存在");
                const targetPost: Post = { ...toHumpFieldObject(searchPost[0]) }

                const [searchStar] = await database.execute(
                    `SELECT * FROM star WHERE post_id='${requestData.postId}' AND user_id='${tokenData.id}';`
                ) as [Array<Star>, any];
                if (searchStar.length === 0) {
                    const newStar: Star = { id: uuid(), postId: requestData.postId, userId: tokenData.id };
                    await database.execute(
                        `INSERT INTO star (id, post_id, user_id) VALUES('${newStar.id}','${newStar.postId}','${newStar.userId}');`
                    );

                    // todo remove comment
                    // if (tokenData.id !== targetPost.userId) {
                    notice(tokenData.id, targetPost.userId, targetPost.id, "点赞了你的动态");
                    // }
                    return ResultUtil.success(newStar, "点赞成功");
                } else {
                    await database.execute(
                        `DELETE FROM star WHERE post_id='${requestData.postId}' AND user_id='${tokenData.id}';`
                    );
                    return ResultUtil.success(null, "取消点赞");
                }
            })
        )

        /**
         * 查看一个动态下的点赞数
         */
        .get("/api/star/count", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: NewStarRequestData = ctx.query as any;
                if (!requestData.postId) throw new ApiError("postId不能为空");

                const [searchPost] = await database.execute(
                    `SELECT id FROM post WHERE id='${requestData.postId}'`
                ) as [Array<any>, any];
                if (searchPost.length === 0) throw new ApiError("目标动态不存在");

                const [searchCount] = await database.execute(
                    `SELECT count(*) as count FROM star WHERE post_id='${requestData.postId}' GROUP BY post_id;`
                ) as [Array<any>, any];

                if (searchCount[0]) return ResultUtil.success(searchCount[0]);
                else return ResultUtil.success({ count: 0 });
            })
        )
}