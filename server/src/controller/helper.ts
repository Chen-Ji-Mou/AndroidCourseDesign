import jwt from 'jsonwebtoken';
import { ApiError } from '../model';
import { CTX, TokenData } from './type';
import config from './../../config';

export function verifyToken(token: string): TokenData {
    if (!token || !token.startsWith("Bearer ")) throw new ApiError("token verify not pass");

    const { id, name } = jwt.verify(token.substring(7), config.secretKey) as any;
    return { id, name };
}

export const handler = (func: (ctx: CTX) => Promise<any>) => {
    return async (ctx: CTX) => {
        const returnValue = await func(ctx);
        ctx.body = returnValue;
    };
};