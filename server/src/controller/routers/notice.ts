import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { database } from '..';
import { Notice, User } from '../model';
import { v4 as uuid } from 'uuid';
import { toHumpFieldObject } from '../../utlis';
import { ResultUtil } from 'ningx';
import { ReadNoticeRequestData } from '../type';
import { ApiError } from './../../model';

export default function addNoticeRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 获取某个用户的所有通知
         */
        .get("/api/notice/all", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const [searchNoticeList] = await database.execute(
                    `SELECT * FROM notice WHERE receiver='${tokenData.id}'`
                ) as [Array<any>, any];
                const noticeList: Array<Notice> = searchNoticeList.map(x => {
                    return {
                        ...toHumpFieldObject(x)
                    }
                })
                if (noticeList.length === 0) return ResultUtil.success([]);
                else return ResultUtil.success(noticeList);
            })
        )

        /**
         * 某个通知变为已读
         */
        .post("/api/notice/read", handler(
            async (ctx) => {
                const token = ctx.header.authorization;
                const tokenData = verifyToken(token);

                const requestData: ReadNoticeRequestData = ctx.request.body;
                if (!requestData.id) throw new ApiError("未传入目标通知id");

                const [searchNotice] = await database.execute(
                    `SELECT * FROM notice WHERE id='${requestData.id}'`
                ) as [Array<any>, any];
                if (searchNotice.length === 0) throw new ApiError("目标通知不存在");
                const targetNotice: Notice = toHumpFieldObject(searchNotice[0]);

                await database.execute(
                    `UPDATE notice SET \`read\`=1 WHERE id='${requestData.id}';`
                ) as [Array<any>, any];
                
                return ResultUtil.success(null);
            })
        )
}
export async function notice(senderId: string, receiverId: string, postId: string, content: Notice['content']) {
    const [searchUser] = await database.execute(
        `SELECT * FROM user WHERE id='${senderId}';`
    ) as [Array<User>, any];

    const newNotice: Notice = {
        id: uuid(),
        sender: searchUser[0].name,
        receiver: receiverId,
        postId, content,
        read: false,
        date: new Date().getTime()
    };
    await database.execute(
        `INSERT INTO notice (id, sender, receiver, post_id, content, \`read\`, date) VALUES('${newNotice.id}', '${newNotice.sender}', '${newNotice.receiver}', '${newNotice.postId}', '${newNotice.content}', ${newNotice.read ? 1 : 0}, '${newNotice.date}');`
    );
}