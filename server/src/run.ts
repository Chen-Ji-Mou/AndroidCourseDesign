import { JsonWebTokenError } from 'jsonwebtoken';
import Koa from 'koa';
import bodyParser from 'koa-bodyparser';
import { router } from './controller';
import { ResultUtil } from 'ningx';
import { ApiError } from './model';

const app = new Koa();

app.use(async (ctx, next) => {
    await next();
});

app.use(async (ctx, next) => {
    // 允许来自所有域名请求
    ctx.set("Access-Control-Allow-Origin", "*");

    // 设置所允许的HTTP请求方法
    ctx.set("Access-Control-Allow-Methods", "OPTIONS, GET, PUT, POST, DELETE");

    // 字段是必需的。它也是一个逗号分隔的字符串，表明服务器支持的所有头信息字段.
    ctx.set("Access-Control-Allow-Headers", "x-requested-with, accept, origin, content-type,authorization");

    // 服务器收到请求以后，检查了Origin、Access-Control-Request-Method和Access-Control-Request-Headers字段以后，确认允许跨源请求，就可以做出回应。

    // Content-Type表示具体请求中的媒体类型信息
    ctx.set("Content-Type", "application/json;charset=utf-8");

    if (ctx.method === 'OPTIONS') {
        ctx.body = '';
    }
    await next();
})

app.use(async (ctx, next) => {
    try {
        await next();
    } catch (e) {
        if (e instanceof ApiError || e instanceof JsonWebTokenError) {
            ctx.response.status = 403
            ctx.body = ResultUtil.error(e, e.message, 403);
        } else {
            console.error("internal server error: ", e);
            ctx.response.status = 500;
            ctx.body = ResultUtil.error(e, "internal server", 500);
        }
    }
})

app.use(bodyParser());

app.use(router.routes());

app.listen(5108, () => {
    console.info(`Server is running at http://localhost:5108`);
})

export default app;