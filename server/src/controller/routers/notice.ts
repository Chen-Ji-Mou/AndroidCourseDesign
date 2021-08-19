import Router from 'koa-router';
import { handler, verifyToken } from '../helper';
import { MySQL } from '../../database';
import { database } from '..';
import { Notice, User } from '../model';
import { v4 as uuid } from 'uuid';

export default function addNoticeRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 获取某个用户的所有通知
         */
        .get("/api/notice/all", handler(
            async (ctx) => {
                // todo
            })
        )

        /**
         * 某个通知变为已读
         */
        .post("/api/notice/read", handler(
            async (ctx) => {
                // todo
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
        `INSERT INTO notice (id, sender, receiver, post_id, content, \`read\`, date) VALUES('${newNotice.id}', '${newNotice.sender}', '${newNotice.receiver}', '${newNotice.postId}', '${newNotice.content}', ${newNotice ? 1 : 0}, '${newNotice.date}');`
    );
}