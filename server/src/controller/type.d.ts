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