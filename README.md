# SmsUrlTriggerApp
개인용 안드로이드 앱: 수신한 SMS를 순차적으로 지정된 URL로 POST 전송합니다.
- UI: 호출 URL 입력(저장), 최근 10개 메시지 표시(상태/재전송)
- 메시지는 큐에 저장되어 순차적으로 처리됩니다 (중복/병렬 호출 방지)
- GitHub Actions로 `assembleDebug` 빌드 후 APK를 업로드하는 워크플로 포함

사용법:
1. 이 저장소를 GitHub에 업로드 (main 브랜치)
2. GitHub Actions가 자동으로 APK를 빌드합니다.
3. Actions 실행 결과에서 아티팩트로 APK를 다운로드하여 설치하세요 (디버그 APK).

권한:
- RECEIVE_SMS, READ_SMS, INTERNET 필요 (런타임 권한 요청)

주의:
- Android 버전과 제조사별로 백그라운드 제약이 있으니 개인 테스트 환경에서 확인하세요.
