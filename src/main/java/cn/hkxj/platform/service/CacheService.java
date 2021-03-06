
package cn.hkxj.platform.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.swing.text.html.Option;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import cn.hkxj.platform.pojo.vo.StatisticsDetailVo;
import cn.hkxj.platform.utils.CacheKeyUtils;
import cn.hkxj.platform.utils.DateUtils;

/**
 * @author zhouqinglai
 * @version version
 * @title CacheService
 * @desc 缓存服务
 * @date: 2019年05月02日
 */
@Service
public class CacheService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 自增值
     */
    private static final Integer INCREMENT_VALUE = 1;

    /**
     * 失效期
     */
    private static final Long EXPIRE_TIME = 30L;

    private static final Long EXPIRE_TIME_FOR_EVERY_DAY = 7L;


    /**
     * 自增
     *
     * @param key
     * @param value
     */
    public void increment(String key, String value) {
        final String statisticsKey = CacheKeyUtils.getStatisticsKeyForEveryMonth(key);
        final String statisticsKeyByTime = CacheKeyUtils.getStatisticsKeyForEveryDay(key, LocalDateTime.now());
        redisTemplate.opsForZSet().incrementScore(statisticsKey, value, INCREMENT_VALUE);
        redisTemplate.opsForZSet().incrementScore(statisticsKeyByTime, value, INCREMENT_VALUE);
        redisTemplate.expire(statisticsKeyByTime, EXPIRE_TIME_FOR_EVERY_DAY, TimeUnit.DAYS);
        redisTemplate.expire(statisticsKey, EXPIRE_TIME, TimeUnit.DAYS);
    }


    /**
     * 根据调用次数倒序查询
     * 支持top n
     *
     * @param key   业务key
     * @param start start index
     * @param end   end index
     * @return List<StatisticsDetailVo>
     */
    public List<StatisticsDetailVo> listStatisticsVoByPage(String key, Integer start, Integer end, boolean isEveryDay, LocalDateTime time) {
        String statisticsKeyForEveryMonthOrDay;
        final String timeOfPattern;
        if (isEveryDay) {
            statisticsKeyForEveryMonthOrDay = CacheKeyUtils.getStatisticsKeyForEveryDay(key, time);
            timeOfPattern = DateUtils.getTimeOfPattern(LocalDateTime.now(), DateUtils.YYYY_MM_DD_PATTERN);
        } else {
            statisticsKeyForEveryMonthOrDay = CacheKeyUtils.getStatisticsKeyForEveryMonth(key);
            timeOfPattern = DateUtils.getTimeOfPattern(LocalDateTime.now(), DateUtils.YYYY_MM_PATTERN);
        }
        final Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(statisticsKeyForEveryMonthOrDay, start, end);
        if (CollectionUtils.isEmpty(typedTuples)) {
            return Collections.emptyList();
        }

        return typedTuples.stream().map(typedTuple -> {
            final String value = typedTuple.getValue();
            final Double score = typedTuple.getScore();
            final StatisticsDetailVo statisticsDetailVo = new StatisticsDetailVo();
            statisticsDetailVo.setUrl(value);
            statisticsDetailVo.setCount(Optional.ofNullable(score).map(Double::longValue).orElse(0L));
            statisticsDetailVo.setDate(timeOfPattern);
            return statisticsDetailVo;
        }).collect(Collectors.toList());
    }

    public Long sizeByBizKey(String key) {
        return Optional.ofNullable(redisTemplate.opsForZSet().zCard(CacheKeyUtils.getStatisticsKeyForEveryMonth(key))).orElse(0L);
    }

    /**
     * 查询指定key的访问统计
     *
     * @param key        业务key
     * @param time       需要查询的时间
     * @param isEveryDay 查每天的count true ，每月的为false
     * @return
     */
    public Long count(String key, LocalDateTime time, boolean isEveryDay) {
        if (isEveryDay) {
            return Optional.ofNullable(redisTemplate.opsForZSet().zCard(CacheKeyUtils.getStatisticsKeyForEveryDay(key, time))).orElse(0L);
        }
        return Optional.ofNullable(redisTemplate.opsForZSet().zCard(CacheKeyUtils.getStatisticsKeyForEveryMonth(key))).orElse(0L);
    }
}
