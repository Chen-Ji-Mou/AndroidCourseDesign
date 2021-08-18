import Router from 'koa-router';
import { handler } from '../helper';
import { MySQL } from '../../database';
import { LoginRequestData, RegisterRequestData, TokenData } from '../type';
import { pickField, ResultUtil } from 'ningx';
import { User } from '../model';
import { v4 as uuid } from 'uuid';
import jwt from 'jsonwebtoken';
import config from './../../../config';

export default function addUserRouter(router: Router<any, {}>, database: MySQL) {
    router
        /**
         * 注册
         */
        .post("/api/user/register", handler(
            async (ctx) => {
                const requestData: RegisterRequestData = ctx.request.body;

                if (!requestData.name) return ResultUtil.error(null, "用户名为空", -1);
                if (!requestData.password) return ResultUtil.error(null, "密码为空", -1);
                if (requestData.password.length < 6) return ResultUtil.error(null, "密码长度小于6位");

                const [searchName] = await database.execute(
                    `SELECT * FROM user WHERE name='${requestData.name}'`
                ) as [Array<User>, any];

                if (searchName.length > 0) return ResultUtil.error(null, "用户名已被注册", -1);

                const newUser: User = { id: uuid(), ...requestData };
                await database.execute(
                    `INSERT INTO user (id, name, password) VALUES('${newUser.id}', '${newUser.name}', '${newUser.password}');`
                );

                const tokenData: TokenData = pickField(newUser, ["id", "name"]);
                return ResultUtil.success(
                    jwt.sign(
                        tokenData,
                        config.secretKey,
                        { expiresIn: "1 days" }
                    ),
                )
            })
        )
        /**
         * 登陆
         */
        .post("/api/user/login", handler(
            async (ctx) => {
                const requestData: LoginRequestData = ctx.request.body;

                if (!requestData.name) return ResultUtil.error(null, "用户名为空", -1);
                if (!requestData.password) return ResultUtil.error(null, "密码为空", -1);
                if (requestData.password.length < 6) return ResultUtil.error(null, "密码长度小于6位");

                const [searchName] = await database.execute(
                    `SELECT * FROM user WHERE name='${requestData.name}' AND password='${requestData.password}'`
                ) as [Array<User>, any];

                if (searchName.length === 0) return ResultUtil.error(null, "用户名不存在或密码错误", -1);

                const loginUser = searchName[0];
                const tokenData: TokenData = pickField(loginUser, ["id", "name"]);
                return ResultUtil.success(
                    jwt.sign(
                        tokenData,
                        config.secretKey,
                        { expiresIn: "1 days" }
                    ),
                )
            })
        )
}

