分布式会话
	用户登录
		二次MD5加密
		参数校验+全局异常处理
	共享Session
		springsession
		redis
		为什么需要分布式session，因为当有很多服务器时，你使用session保存的用户数据，只会存在于当前这台主机，如果有反向代理，下次登录登录的不是这台服务器，那么用户信息是全部没有的，这时就需要分布式session了
			session复制
				
					无需修改代码，只需要配置tomcat；但是session同步会占用内网带宽，多结点tomcat同步性能下降；session占用内存，无法有效水平扩展
			前端存储
				不占用服务器内存，但存在安全风险，数据大小受cookie限制，占用外网带宽
			session粘滞
				无需修改代码，可水平扩展。但是增加新机器，会重新hash，导致重新登录，应用重启也会导致重新登录
			后端集中存储
				安全，容易水平扩展，但较复杂需要改代码

功能开发
	商品列表
	商品详情
	秒杀
	订单详情

系统压测
	JMeter入门
	自定义变量
	正式压测

页面优化
	缓存
		url缓存
		对象缓存
	静态化分离：页面静态化，就是将不常改变的页面资源缓存在浏览器中，其他经常需要改变的在通过请求发送改变

服务优化
	RabbitMQ消息队列：解决用户下单时的体验，请求进入队列缓存，异步下单，并且轮询查询秒杀结果
	接口优化
		1.通过redis预减库存减少数据库的访问
		2.内存标记减少redis的访问
	Redis实现分布式锁
		lua脚本实现操作原子性
			1.在redis端写好lua脚本，然后java客户端调用，易调用、难修改
			2.在java客户端写好，运行发送到Redis执行，调用耗时、易修改，且吞吐量影响大

安全优化
	隐藏秒杀地址，防止黄牛在http明文传输上抓包，提前知道地址进行机器秒杀，只有在时间段内点击秒杀按钮才能知道
	验证码，同样隐藏秒杀地址还是会有疏漏，就是如果提前知道封装一层的秒杀地址去请求得到我们的拼接字符串，并且知道拼接规则，这时就要用验证码再次进行安全校验
	接口限流算法，再次提高服务器的稳定性，以及接口请求的防刷
		1.计数器算法：它是限流算法中最简单最容易的一种算法，比如我们要求某一个接口，1分钟内的请求不能超过10次，我们可以在开始时设置一个计数器，每次请求，该计数器+1；如果该计数器的值大于10并且与第一次请求的时间间隔在1分钟内，那么说明请求过多；如果该请求与第一次请求的时间间隔大于1分钟，并且该计数器的值还在限流范围内，那么重置该计数器
			但此算法有缺陷，如果定死一个非极限值的请求界限，那么只要超过一点就会报错，此时离峰值还是能撑一会的。所以我们会设置一个范围峰值的百分之70左右
			还有缺陷就是在，在规定单位时间内的前半段全部处理完了限定的请求次数那么后半段时间全部是浪费的；还有在单位时间内快结束请求数飙升到规定的临界，但未超过，过渡到下一个单位时间立马又有同样的数量的请求过来，此时虽然处于两段单位时间，但对于服务器来说这几乎没有间隔
		2.漏桶算法：这个算法很简单。首先，我们有一个固定容量的桶，有水进来，也有水出去。对于流进来的水，我们无法预计共有多少水流进来，也无法预计流水速度，但
对于流出去的水来说，这个桶可以固定水流的速率，而且当桶满的时候，多余的水会溢出来。
		3.令牌桶算法：从上图中可以看出，令牌算法有点复杂，桶里存放着令牌token。桶一开始是空的，token以固定的速率r往桶里面填充，直到达到桶的容量，多余的token会
被丢弃。每当一个请求过来时，就会尝试着移除一个token，如果没有token，请求无法通过。
		ThreadLocal解决的是每个线程绑定自己的值，如果在一个公共线程中存放用户信息会导致用户信息的紊乱
 * 所以我们需要每个登录的用户的信息都存放在自己的线程中，从而避免线程安全问题
 * ThreadLocal是Thread的一个字段，初始值为null，当调用get、set方法时会被初始化得到，
 * 内部存放的结构是hashmap即ThreadLocalMap，key是当前线程，value就是set进行的对象

库存超卖
	1.数据库加入唯一索引，防止用户重复购买
	2.sql语句的逻辑及CAS组合，防止库存超卖

整体优化思路
	1.主要就是减少与最底层的数据库交互次数
	2.增强数据库的性能，比如说分库分表、数据库的集群
