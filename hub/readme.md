## 목적

기존 Zigbee Hub를 저렴한 블루투스 기기로 대체하여 사용할수 있으면서 다른 Zigbee Hub로 대체하여도 정상적으로 작동하는 Hub 제작

## 구성

1번 시나리오

```markdown
[Google Home Cloud]
[SmartThings Cloud(추후)]  ->  [CHome Server]  ->  [LocalHub(esp32)] ->  [Other BlueTooth Device]
[Alexa Smart Home(추후)]
```

2.번 시나리오

```markdown
					[Google Home Cloud]

					        ||
						    ▽
																									
[CHome Server]  ->  [LocalHub(esp32)] ->  [Other BlueTooth Device]
```

### 선택 시나리오

1번 시나리오

### 이유

저희의 앱을 사용해야 할 이유가 생기니까.

## 핵심

- LocalHub와 핸드폰은 통신이 가능해야 한다
- LocalHub(esp32)는 언제든지 대체될 수 있다.
- 다른 Hub가 와도 BLE Device는 정상적으로 동작해야 한다
- BLE Device가 우리 제품이 아니더라도 정상적으로 연결이 되어야 한다
- CHome Server가 Google Home Cloud와 연동이 되어야 한다
- Google Home 앱 에서 제어가 가능해야 하지만 CHome  앱 에서도 제어가 가능해야 한다

## 우선 순위

1. LocalHub & BlueTooth Device 제작
2. CHome Server 제작
3. Google Home Cloud 연동