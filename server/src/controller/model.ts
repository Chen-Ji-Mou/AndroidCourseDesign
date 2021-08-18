export class User {
    id: string;
    name: string;
    password: string;

    constructor(id?: string, name?: string, password?: string) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}

export class Post {
    id: string;
    content: string;
    userId: string;
    pictures: Array<string>;
    date: number;

    constructor(id?: string, content?: string, userId?: string, pictures?: Array<string>, date?: number) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.pictures = pictures;
        this.date = date;
    }
}