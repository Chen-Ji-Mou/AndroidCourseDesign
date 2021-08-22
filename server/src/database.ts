import mysql, { Pool, PoolConfig, FieldInfo } from 'mysql'
import { ApiError } from './model';

export class MySQL {
    pool: Pool
    constructor(config: PoolConfig) {
        this.pool = mysql.createPool({ ...config, connectionLimit: 10 });
    }

    async execute(sql: string): Promise<[any, FieldInfo[]]> {
        return new Promise((resolve, reject) => {
            const begin = new Date().getTime();
            this.pool.query(sql, function (error, results, fields) {
                const end = new Date().getTime();
                console.info(`[MySQL] execute <${sql}> time: ${end - begin}ms`);
                if (error) reject("Server sql error");
                resolve([results, fields ? fields : []])
            });
        });
    }

    async insert(table: string, data: Record<string, any>): Promise<[any, FieldInfo[]]> {
        const sql = `INSERT INTO ${table} (${Object.keys(data)}) VALUES (${Object.values(data).map(x => `'${x}'`)})`;
        return await this.execute(sql);
    }
}