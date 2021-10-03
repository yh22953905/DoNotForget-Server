package com.hungrybrothers.alarmforsubscription.redis;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import com.hungrybrothers.alarmforsubscription.common.CommonTest;

@Disabled
public class RedisTest extends CommonTest {
	@Autowired
	RedisTemplate redisTemplate;

	@Test
	@DisplayName("기본 명령어 테스트")
	public void command() {
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		valueOps.set("key1", "key1value");
		valueOps.set("key2", "key2value");

		// Key 타입 조회
		assertEquals(DataType.STRING, redisTemplate.type("key1"));
		// Key 개수 반환
		assertSame(2L, redisTemplate.countExistingKeys(Arrays.asList("key1", "key2", "key3")));
		// Key 존재 여부 확인
		assertTrue(redisTemplate.hasKey("key1"));
		// Key 만료 날짜 세팅
		assertTrue(redisTemplate.expireAt("key1", Date.from(LocalDateTime.now().plusSeconds(10L).atZone(ZoneId.systemDefault()).toInstant())));
		// Key 만료 시간 조회
		assertThat(redisTemplate.getExpire("key1"), greaterThan(0L));
		// Key 만료 시간 해제
		assertTrue(redisTemplate.persist("key1"));
		// Key 만료 시간이 세팅 안되어 있는 경우
		assertSame(-1L, redisTemplate.getExpire("key1"));
		// Key 삭제
		assertTrue(redisTemplate.delete("key1"));
		// Key 일괄 삭제
		assertThat(redisTemplate.delete(Arrays.asList("key1", "key2", "key3")), greaterThan(0L));
	}

	@Test
	@DisplayName("String Structure 명령어 테스트 - set, get")
	public void opsValue() {
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		Collection<String> cacheKeys = new ArrayList<>();
		String cacheKey = "value_";
		for (int i = 0; i < 10; i++) {
			cacheKeys.add(cacheKey + i);
			valueOps.set(cacheKey + i, String.valueOf(i), 60, TimeUnit.SECONDS);
		}
		List<String> values = valueOps.multiGet(cacheKeys);
		assertNotNull(values);
		assertEquals(10, values.size());
	}

	@Test
	@DisplayName("List Structure 명령어 테스트 - lpush, llen, lrange, lpop, rpop")
	public void opsList() {
		ListOperations<String, String> listOps = redisTemplate.opsForList();
		String cacheKey = "valueList";
		for (int i = 0; i < 10; i++) {
			listOps.leftPush(cacheKey, String.valueOf(i));
		}
		assertSame(DataType.LIST, redisTemplate.type(cacheKey));
		assertSame(10L, listOps.size(cacheKey));
		assertEquals("[9, 8, 7, 6, 5, 4, 3, 2, 1, 0]", listOps.range(cacheKey, 0, 10).toString());
		assertEquals("0", listOps.rightPop(cacheKey));
		assertEquals("9", listOps.leftPop(cacheKey));
		assertEquals(true, redisTemplate.delete(cacheKey));
	}

	@Test
	@DisplayName("Hash Structure 명령어 테스트 - 순서 없음, key 중복 허용 안함, value 중복 허용")
	public void opsHash() {
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
		String cacheKey = "valueHash";
		for (int i = 0; i < 10; i++) {
			hashOps.put(cacheKey, "key_" + i, "value_" + i);
		}
		assertSame(DataType.HASH, redisTemplate.type(cacheKey));
		assertSame(10L, hashOps.size(cacheKey));
		Set<String> hKeys = hashOps.keys(cacheKey);
		assertEquals("value_5", hashOps.get(cacheKey, "key_5"));
		assertSame(1L, hashOps.delete(cacheKey, "key_5"));
		assertSame(null, hashOps.get(cacheKey, "key_5"));
	}

	@Test
	@DisplayName("Set Structure 명령어 테스트 - 순서 없음, value 중복 허용 안함")
	public void opsSet() {
		SetOperations<String, String> setOps = redisTemplate.opsForSet();
		String cacheKey = "valueSet";
		for (int i = 0; i < 10; i++) {
			setOps.add(cacheKey, String.valueOf(i));
		}
		assertSame(DataType.SET, redisTemplate.type(cacheKey));
		assertSame(10L, setOps.size(cacheKey));
		assertNotEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]", setOps.members(cacheKey).toString());
		assertTrue(setOps.isMember(cacheKey, "5"));
	}

	@Test
	@DisplayName("SortedSet Structure 명령어 테스트 - 순서 있음, value 중복 허용 안함")
	public void opsSortedSet() {
		ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
		String cacheKey = "valueZSet";
		for (int i = 0; i < 10; i++) {
			zSetOps.add(cacheKey, String.valueOf(i), i);
		}
		assertSame(DataType.ZSET, redisTemplate.type(cacheKey));
		assertSame(10L, zSetOps.size(cacheKey));
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]", zSetOps.range(cacheKey, 0, 10).toString());
		assertSame(0L, zSetOps.reverseRank(cacheKey, "9"));
	}

	@Test
	@DisplayName("Geo Structure 명령어 테스트 - 좌표 정보 처리, 타입은 ZSet 으로 저장")
	public void opsGeo() {
		GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
		String[] cities = {"서울", "부산"};
		String[][] gu = {{"강남구", "서초구", "관악구", "동작구", "마포구"}, {"사하구", "해운대구", "영도구", "동래구", "수영구"}};
		Point[][] pointGu = {{new Point(10, -10), new Point(11, -20), new Point(13, 10), new Point(14, 30), new Point(15, 40)}, {new Point(-100, 10), new Point(-110, 20), new Point(-130, 80), new Point(-140, 60), new Point(-150, 30)}};
		String cacheKey = "valueGeo";

		redisTemplate.delete(cacheKey);

		for (int x = 0; x < cities.length; x++) {
			for (int y = 0; y < 5; y++) {
				geoOps.add(cacheKey, pointGu[x][y], gu[x][y]);
			}
		}

		Distance distance = geoOps.distance(cacheKey, "강남구", "동작구");
		assertNotNull(distance);
		assertEquals(4469610.0767, distance.getValue(), 4);
		List<Point> position = geoOps.position(cacheKey, "동작구");
		assertNotNull(position);
		for (Point point : position) {
			assertEquals(14.00001847743988D, point.getX(), 4);
			assertEquals(30.000000249977013D, point.getY(), 4);
		}
	}

	@Test
	@DisplayName("HyperLogLog Structure 명령어 테스트 - 집합 내 원소의 개수 추정, 타입은 String 으로 저장")
	public void opsHyperLogLog() {
		HyperLogLogOperations<String, String> hyperLogLogOps = redisTemplate.opsForHyperLogLog();
		String cacheKey = "valueHyperLogLog";
		String[] arr1 = {"1", "2", "2", "3", "4", "5", "5", "5", "5", "6", "7", "7", "7"};
		hyperLogLogOps.add(cacheKey, arr1);
		assertSame(7L, hyperLogLogOps.size(cacheKey));
		redisTemplate.delete(cacheKey);
	}
}
