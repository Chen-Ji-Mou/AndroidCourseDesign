import Koa from 'koa';
import Router from 'koa-router';

export type CTX = Koa.ParameterizedContext<
    any,
    Router.IRouterParamContext<any, {}>,
    any
>;

export type TokenData = { id: string, name: string };

export type RegisterRequestData = { name: string, password: string };
export type LoginRequestData = RegisterRequestData;

export type NewPostRequestData = { content: string, pictures: Array<string> };
export type GetUserPostRequestDate = { userId: string };
export type DeletePostRequestData = { id: string };

export type UploadPictureRequestData = { base64: string };
export type GetPictureRequestData = { id: string };

export type NewCommentRequestData = { postId: string, content: string };
export type CommentListOfPostRequestData = { postId: string };