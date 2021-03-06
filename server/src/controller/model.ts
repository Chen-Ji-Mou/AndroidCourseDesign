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

export class Picture {
    id: string;
    base64: string;

    constructor(id?: string, base64?: string) {
        this.id = id;
        this.base64 = base64;
    }
}

export class Comment {
    id: string;
    content: string;
    userId: string;
    postId: string;
    date: number;

    constructor(id?: string, content?: string, userId?: string, postId?: string, date?: number) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.date = date;
    }
}

export class Star {
    id: string;
    postId: string;
    userId: string;

    constructor(id?: string, postId?: string, userId?: string) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
    }
}

export class Notice {
    id: string;
    sender: string;
    receiver: string;
    postId: string;
    content: "点赞了你的动态" | "评论了你的动态";
    read: boolean;
    date: number;

    constructor(id?: string, sender?: string, receiver?: string, postId?: string, content?: Notice['content'], read?: boolean, date?: number) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.postId = postId;
        this.content = content;
        this.read = read;
        this.date = date;
    }
}