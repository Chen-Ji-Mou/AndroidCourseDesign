import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { NewStarRequestData } from '../type';
import { ApiError } from '../../model';
import { Star } from '../model';
import { v4 as uuid } from 'uuid';
import { ResultUtil } from 'ningx';

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
                    `SELECT id FROM post WHERE id='${requestData.postId}'`
                ) as [Array<any>, any];
                if (searchPost.length === 0) throw new ApiError("目标动态不存在");

                const [searchStar] = await database.execute(
                    `SELECT * FROM star WHERE post_id='${requestData.postId}' AND user_id='${tokenData.id}';`
                ) as [Array<Star>, any];
                if (searchStar.length === 0) {
                    const newStar: Star = { id: uuid(), postId: requestData.postId, userId: tokenData.id };
                    await database.execute(
                        `INSERT INTO star (id, post_id, user_id) VALUES('${newStar.id}','${newStar.postId}','${newStar.userId}');`
                    );
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
                // todo
            })
        )
}