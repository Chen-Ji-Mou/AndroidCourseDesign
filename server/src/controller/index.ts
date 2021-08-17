import Router from "koa-router";
import { MySQL } from "../database";
import config from "../../config";
import addUserRouter from './routers/user';
import addCommentRouter from './routers/comment';
import addNoticeRouter from './routers/notice';
import addPictureRouter from './routers/picture';
import addPostRouter from './routers/post';
import addStarRouter from './routers/star';

export const router = new Router();
export const database = new MySQL(config.mysql);

addCommentRouter(router, database);
addNoticeRouter(router, database);
addPictureRouter(router, database);
addPostRouter(router, database);
addStarRouter(router, database);
addUserRouter(router, database);

export default router;