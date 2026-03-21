## 2026-03-11: Command/Result 위치 고민

**문제**: Command와 Result를 `application`에 둘지 `port.in`에 둘지 고민

**원인**:
- Command/Result는 구체 클래스인데 인터페이스 레이어(Port)에 두는 게 맞나?
- Adapter가 Port를 넘어 Application을 직접 보는 게 찝찝함

**해결**: `port.in`으로 결정
- 의존성: Adapter → Port ← Application (계약 우회 방지)

**상세**: [링크](./command-result-placement.md)