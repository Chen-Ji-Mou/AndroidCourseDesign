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