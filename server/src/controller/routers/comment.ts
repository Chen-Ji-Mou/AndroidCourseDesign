import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { CommentListOfPostRequestData, DeleteCommentRequestData, NewCommentRequestData } from '../type';
import { ApiError } from './../../model';
import { v4 as uuid } from 'uuid';
import { Comment, Post } from '../model';
import { ResultUtil } from 'ningx';
import { toHumpFieldObject } from '../../utlis';
import { notice } from './notice';

export default function addCommentRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 增加一个评论
         */
        .post("/api/comment", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: NewCommentRequestData = ctx.request.body;
                if (!requestData.content) throw new ApiError("content不能为空");
                if (!requestData.postId) throw new ApiError("postId不能为空");

                const [searchPost] = await database.execute(
                    `SELECT * FROM post WHERE id='${requestData.postId}'`
                ) as [Array<any>, any];
                if (searchPost.length === 0) throw new ApiError("目标动态不存在");
                const targetPost: Post = { ...toHumpFieldObject(searchPost[0]) }

                const newComment: Comment = {
                    id: uuid(),
                    postId: requestData.postId,
                    userId: tokenData.id,
                    content: requestData.content,
                    date: new Date().getTime()
                };

                await database.execute(
                    `INSERT INTO comment (id, post_id, user_id, content, date) VALUES('${newComment.id}','${newComment.postId}','${newComment.userId}','${newComment.content}','${newComment.date}');`
                );

                // todo remove comment
                // if (tokenData.id !== targetPost.userId) {
                await notice(tokenData.id, targetPost.userId, requestData.postId, "点赞了你的动态");
                // }
                
                return ResultUtil.success(newComment);
            })
        )

        /**
         * 查看一个动态下的所有评论
         */
        .get("/api/comment/list", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: CommentListOfPostRequestData = ctx.query as any;
                if (!requestData.postId) throw new ApiError("postId不能为空");

                const [searchPost] = await database.execute(
                    `SELECT id FROM post WHERE id='${requestData.postId}'`
                ) as [Array<any>, any];
                if (searchPost.length === 0) throw new ApiError("目标动态不存在");

                const [searchCommentList] = await database.execute(
                    `SELECT * FROM comment WHERE post_id='${requestData.postId}'`
                ) as [Array<any>, any];
                const commentList: Array<Comment> = searchCommentList.map(x => {
                    return {
                        ...toHumpFieldObject(x)
                    }
                })

                return ResultUtil.success(commentList);
            })
        )

        /** 
         * 删除某条评论
         */
        .delete("/api/comment", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: DeleteCommentRequestData = ctx.request.body;
                if (!requestData.id) throw new ApiError("评论id不能为空");

                const [searchComment] = await database.execute(
                    `SELECT * FROM comment WHERE id='${requestData.id}'`
                ) as [Array<any>, any];
                if (searchComment.length === 0) throw new ApiError("目标评论不存在");

                const targetComment: Comment = { ...toHumpFieldObject(searchComment[0]) };
                if (targetComment.userId !== tokenData.id) throw new ApiError("不能删除别人的评论");

                await database.execute(
                    `DELETE FROM comment WHERE id='${requestData.id}'`
                );

                return ResultUtil.success(null);
            })
        )
}
