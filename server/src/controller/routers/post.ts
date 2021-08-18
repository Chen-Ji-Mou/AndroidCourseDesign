import Router from "koa-router";
import { MySQL } from "../../database";
import { handler } from "../helper";

export default function addPostRouter(router: Router<any, {}>, database: MySQL) {
    router
        /** 
         * 获取首页动态列表
         */
        .post("/api/post/hello", handler(
            async (ctx) => {
                // todo
            })
        )

        /** 
         * 获取某个人的动态
         */
        .post("/api/post/profile", handler(
            async (ctx) => {
                // todo
            })
        )

        /** 
         * 发布新动态
         */
        .post("/api/post", handler(
            async (ctx) => {
                // todo
            })
        )

        /** 
         * 删除某条动态
         */
        .delete("/api/post", handler(
            async (ctx) => {
                // todo
            })
        )
}
