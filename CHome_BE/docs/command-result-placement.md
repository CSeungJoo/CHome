# Command와 Result는 Port에 두어야 하는가, Application에 두어야 하는가?

## TL;DR
**Command와 Result는 Port 패키지에 위치해야 합니다.** 이들은 UseCase 인터페이스와 함께 외부 세계와의 계약(Contract)을 구성하는 요소이기 때문입니다.

---

## 문제 상황

헥사고날 아키텍처(포트-어댑터 패턴)를 적용할 때, 다음과 같은 구조를 자주 볼 수 있습니다:

```java
// port/in/FindAccessibleHubsUseCase.java
package hub.port.in;

import hub.application.command.FindAccessibleHubsCommand;  // ⚠️
import hub.application.result.FindAccessibleHubsResult;    // ⚠️

public interface FindAccessibleHubsUseCase {
    FindAccessibleHubsResult execute(FindAccessibleHubsCommand command);
}
```

이 구조의 문제점은 **Port가 Application에 의존**한다는 것입니다.

---

## 헥사고날 아키텍처의 핵심 원칙

### 의존성 방향 규칙

```
외부(Adapter) → Port(Interface) → Application(구현체) → Domain
```

- **Port는 인터페이스**입니다
- **Application은 Port를 구현**합니다
- 따라서 Application이 Port에 의존해야 하며, **그 반대는 안 됩니다**

현재 구조는 이를 위반합니다:

```
외부(Adapter) → Application(Command/Result)  // ❌ 잘못된 의존성
```

---

## Command와 Result의 본질

### Command와 Result는 무엇인가?

```java
public interface FindAccessibleHubsUseCase {
    Result execute(Command command);
}
```

이 인터페이스는 다음을 말하고 있습니다:
> "나를 사용하려면 Command를 주세요. 그러면 Result를 드리겠습니다."

**Command와 Result는 UseCase 인터페이스와 함께 하나의 완전한 계약(Contract)을 구성합니다.**

### 계약의 일부라는 증거

1. **함께 변경됩니다**
    - UseCase의 입출력이 바뀌면 Command/Result도 함께 바뀝니다
    - 이들은 하나의 응집된 단위입니다

2. **외부(Adapter)가 알아야 하는 정보입니다**
    - REST Controller, gRPC Service, CLI Handler 등
    - 모든 Adapter가 이 계약을 보고 Port를 사용합니다

3. **구현 세부사항이 아닙니다**
    - Application 내부에서 어떻게 처리하는지는 중요하지 않습니다
    - 중요한 것은 "무엇을 주고받는가"입니다

---

## 구조

### 변경된 구조

```
hub/
  ├── port/
  │   ├── in/
  │   │   ├── FindAccessibleHubsUseCase.java
  │   │   ├── FindAccessibleHubsCommand.java      ✅
  │   │   ├── FindAccessibleHubsResult.java       ✅
  │   │   ├── CreateHubUseCase.java
  │   │   ├── CreateHubCommand.java               ✅
  │   │   └── CreateHubResult.java                ✅
  │   └── out/
  │       └── HubRepository.java
  ├── application/
  │   ├── FindAccessibleHubsService.java
  │   └── CreateHubService.java
  ├── adapter/
  │   ├── in/web/HubController.java
  │   └── out/persistence/HubJpaAdapter.java
  └── domain/
      └── Hub.java
```

### 의존성 흐름

```
HubController (Adapter)
    ↓ depends on
FindAccessibleHubsUseCase (Port Interface)
FindAccessibleHubsCommand (Port DTO)
FindAccessibleHubsResult (Port DTO)
    ↑ implemented by
FindAccessibleHubsService (Application)
```

---

## 실제 코드 예시

### Port 패키지

```java
// port/in/FindAccessibleHubsUseCase.java
package hub.port.in;

public interface FindAccessibleHubsUseCase {
    FindAccessibleHubsResult execute(FindAccessibleHubsCommand command);
}

// port/in/FindAccessibleHubsCommand.java
package hub.port.in;

public record FindAccessibleHubsCommand(
    String userId,
    int page,
    int size
) {}

// port/in/FindAccessibleHubsResult.java
package hub.port.in;

import java.util.List;

public record FindAccessibleHubsResult(
    List<HubInfo> hubs,
    int totalCount
) {
    public record HubInfo(
        String hubId,
        String name,
        String status
    ) {}
}
```

### Application 패키지 (Port 구현)

```java
// application/FindAccessibleHubsService.java
package hub.application;

import hub.port.in.FindAccessibleHubsUseCase;
import hub.port.in.FindAccessibleHubsCommand;
import hub.port.in.FindAccessibleHubsResult;
import hub.port.out.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAccessibleHubsService implements FindAccessibleHubsUseCase {
    
    private final HubRepository hubRepository;
    
    @Override
    public FindAccessibleHubsResult execute(FindAccessibleHubsCommand command) {
        // 비즈니스 로직 구현
        var hubs = hubRepository.findAccessibleHubs(
            command.userId(), 
            command.page(), 
            command.size()
        );
        
        return new FindAccessibleHubsResult(hubs, hubs.size());
    }
}
```

### Adapter 패키지 (Port 사용)

```java
// adapter/in/web/HubController.java
package hub.adapter.in.web;

import hub.port.in.FindAccessibleHubsUseCase;
import hub.port.in.FindAccessibleHubsCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {
    
    private final FindAccessibleHubsUseCase findAccessibleHubsUseCase;
    
    @GetMapping
    public ResponseEntity<?> getAccessibleHubs(
        @RequestParam String userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        var command = new FindAccessibleHubsCommand(userId, page, size);
        var result = findAccessibleHubsUseCase.execute(command);
        
        return ResponseEntity.ok(result);
    }
}
```

---

## 이렇게 했을 때의 장점

### 1. 올바른 의존성 방향

```
Adapter → Port ← Application
```

- Port가 가장 안정적인 레이어
- Application과 Adapter 모두 Port에 의존
- Port는 아무것에도 의존하지 않음

### 2. 재사용성

동일한 Port(UseCase, Command, Result)를:
- REST API에서 사용
- gRPC Service에서 사용
- CLI에서 사용
- Batch Job에서 사용

모두 같은 계약을 공유합니다.

### 3. 테스트 용이성

```java
@Test
void testFindAccessibleHubs() {
    // Given
    var command = new FindAccessibleHubsCommand("user123", 0, 10);
    var mockUseCase = mock(FindAccessibleHubsUseCase.class);
    
    // When
    when(mockUseCase.execute(command))
        .thenReturn(new FindAccessibleHubsResult(List.of(), 0));
    
    // Then
    // Port만 알면 테스트 가능
}
```

### 4. 변경의 이유가 명확함

| 위치 | 변경 이유 |
|------|----------|
| **Port** (Command/Result) | 외부 인터페이스 요구사항이 바뀔 때 |
| **Application** | 비즈니스 로직이 바뀔 때 |

Command를 Application에 두면, 비즈니스 로직 변경 시 외부 계약도 영향받을 수 있습니다.

### 5. 명확한 경계

```
port/in/  ← "이것이 우리가 제공하는 기능입니다"
```

누구나 Port 패키지만 보면:
- 시스템이 제공하는 모든 UseCase
- 각 UseCase의 입출력
- 전체 Public API

를 한눈에 파악할 수 있습니다.

---

## 반론에 대한 답변

### "Application에서만 사용하는데 Port에 두는 게 맞나요?"

**답:** Command/Result는 Application에서만 사용하는 것이 아닙니다.
- Adapter(Controller, CLI 등)에서 **생성**합니다
- Application에서 **처리**합니다
- Port는 이 둘 사이의 **계약**입니다

### "Port가 너무 복잡해지지 않나요?"

**답:** Port는 복잡해져야 합니다. Port는 시스템의 Public API입니다.
- 모든 외부 진입점이 여기 정의됩니다
- 복잡도는 숨기는 게 아니라 명확히 드러내야 합니다
- 대신 각 Command/Result는 단순해야 합니다 (순수 DTO)

### "UseCase가 많아지면 파일이 너무 많아지지 않나요?"

**답:**
1. IDE의 검색/필터로 충분히 관리 가능합니다
2. 필요하다면 UseCase별로 패키지를 만들 수 있습니다:

```
port/in/
  ├── findaccessiblehubs/
  │   ├── UseCase.java
  │   ├── Command.java
  │   └── Result.java
  └── createhub/
      ├── UseCase.java
      ├── Command.java
      └── Result.java
```

---

## Command/Result의 역할 재정의

### Command는 무엇인가?

- ✅ 불변 데이터 전달 객체 (DTO)
- ✅ UseCase 호출에 필요한 모든 정보
- ❌ **검증 로직 포함 X** (Adapter에서 처리)
- ❌ **비즈니스 로직 포함 X** (Domain에서 처리)

```java
// ✅ 좋은 Command
public record CreateHubCommand(
    String name,
    String ownerId,
    String location
) {}

// ❌ 나쁜 Command
public record CreateHubCommand(
    String name,
    String ownerId,
    String location
) {
    public CreateHubCommand {
        if (name == null || name.isBlank()) {  // ← Adapter의 책임
            throw new IllegalArgumentException();
        }
        if (name.length() > 50) {  // ← Domain의 책임
            throw new BusinessRuleException();
        }
    }
}
```

### Result는 무엇인가?

- ✅ 불변 응답 객체 (DTO)
- ✅ UseCase 실행 결과
- ✅ Adapter가 필요한 모든 정보
- ❌ **UI 표현 로직 포함 X** (Adapter에서 변환)

---

## 결론

**Command와 Result는 Port 패키지에 위치해야 합니다.**

### 핵심 이유

1. **계약의 일부**: UseCase와 함께 외부 인터페이스를 정의
2. **의존성 방향**: Port는 가장 안정적이어야 하며, Application에 의존해서는 안 됨
3. **재사용성**: 여러 Adapter가 동일한 계약을 공유
4. **명확성**: Port 패키지가 시스템의 Public API를 완전히 표현

### 권장 구조

```
port/in/
  ├── SomeUseCase.java
  ├── SomeCommand.java      ✅ Port에 위치
  └── SomeResult.java       ✅ Port에 위치

application/
  └── SomeService.java      (Port를 구현)

adapter/in/
  └── SomeController.java   (Port를 사용)
```