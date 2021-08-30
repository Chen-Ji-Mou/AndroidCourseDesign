import Koa from 'koa';
import Router from 'koa-router';

type CTX = Koa.ParameterizedContext<any, Router.IRouterParamContext<any, {}>, any>

export interface ResultVO {
    data: any;
    code: number;
    msg: string;
    error?: any;
}

export class ResultUtil {
    static success(data: any, msg: string = "request successful", code: number = 0) {
        return { data, code, msg } as ResultVO;
    }

    static error(error: any, msg?: string, code?: number) {
        return { error, code, msg } as ResultVO;
    }

    static self(self: any) {
        return self;
    }
}

export function toHump(target: string, separator?: string): string {
    if (separator === undefined) separator = '_';
    const strs = target.split(separator);

    for (let i = 1; i < strs.length; ++i) {
        strs[i] = strs[i].charAt(0).toUpperCase() + strs[i].substring(1);
    }
    return strs.join('');
}

export function toHumpFieldObject<T>(object: T) {
    const keys = Object.keys(object);
    const humpObject = JSON.parse(JSON.stringify(object));
    keys.forEach(x => {
        humpObject[x] = undefined;
        humpObject[toHump(x)] = object[x];
    });
    return humpObject as T;
}

export function fillZeroAtIntegerFront(number: number, len: number) {
    let res = number as unknown as string;
    while (res.length < len) {
        res = "0" + res;
    }
    return res;
}