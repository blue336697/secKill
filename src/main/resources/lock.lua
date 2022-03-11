--[[ 执行get请求，得到key的第一个 跟随机值比较]]
if redis.call("get", KEYS[1]) == ARGV[1] then
    return redis.call("del",KEYS[1]) --[[ 相同就删除 ]]
else
    return 0
end