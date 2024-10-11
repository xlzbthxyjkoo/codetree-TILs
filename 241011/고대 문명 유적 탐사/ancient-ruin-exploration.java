import java.util.*;

public class Main {
    static final int N_large = 5; //전체 격자 크기
    static final int N_small = 3; //회전시킬 격자 크기

    //전체 격자 정의
    static class Board{
        int [][] a = new int[N_large][N_large];

        //생성자. 격자에 유물 조각 초기화
        public Board() {
            for(int i = 0; i < N_large; i++) {
                for(int j = 0; j < N_large; j++) {
                    a[i][j] = 0;
                }
            }
        }

        //주어진 r,c가 고대 문명 격자 범위 안에 있는지 확인하는 함수
        private boolean inRange(int r, int c) {
            //r < 0 || r >= N_large || c < 0 || c >= N_large
            return 0 <= r && r < N_large && 0 <= c && c < N_large;
        }

        //현재 격자에서 dr, dc를 좌측 상단으로 하여 시계방향 90도 회전 cnt번 시행했을 때 결과 return
        public Board rotate(int dr, int dc, int cnt) {
            Board result = new Board();
            for(int i = 0; i < N_large; i++) {
                for(int j = 0; j < N_large; j++) {
                    result.a[i][j] = this.a[i][j];
                }
            }
            for(int k = 0; k < cnt; k++) {
                //dr, dc를 좌측상단으로 하여 시계방향 90도 회전
                int tmp = result.a[dr + 0][dc + 2];
                result.a[dr + 0][dc + 2] = result.a[dr + 0][dc + 0];
                result.a[dr + 0][dc + 0] = result.a[dr + 2][dc + 0];
                result.a[dr + 2][dc + 0] = result.a[dr + 2][dc + 2];
                result.a[dr + 2][dc + 2] = tmp;
                tmp = result.a[dr + 1][dc + 2];
                result.a[dr + 1][dc + 2] = result.a[dr + 0][dc + 1];
                result.a[dr + 0][dc + 1] = result.a[dr + 1][dc + 0];
                result.a[dr + 1][dc + 0] = result.a[dr + 2][dc + 1];
                result.a[dr + 2][dc + 1] = tmp;
            }
            return result;
        }

        //현재 격자에서 유물 획득
        //새로운 유물 조각을 채우는 것은 여기서 고려하지 않음
        public int calcScore() {
            int score = 0;
            boolean[][] visit = new boolean[N_large][N_large];
            int[] dr = {0, 1, 0, -1};
            int[] dc = {1, 0, -1, 0};

            for(int i = 0; i < N_large; i++) {
                for(int j = 0; j < N_large; j++) {
                    if(!visit[i][j]) {
                        //BFS를 활용한 Flood Fill 알고리즘을 사용하여 visit 배열 채우기
                        //trace 안에 조각들의 위치가 저장
                        Queue<int[]> Q = new LinkedList<>();
                        Queue<int[]> trace = new LinkedList<>();
                        Q.offer(new int[]{i, j});
                        trace.offer(new int[]{i, j});
                        visit[i][j] = true;
                        while(!Q.isEmpty()) {
                            int[] cur = Q.poll(); //remove
                            for(int k = 0; k <4; k++) {
                                int nr = cur[0] + dr[k];
                                int nc = cur[1] + dc[k];
                                if(inRange(nr, nc) && a[nr][nc] == a[cur[0]][cur[1]] && !visit[nr][nc]) {
                                    Q.offer(new int[]{nr, nc});
                                    trace.offer(new int[]{nr, nc});
                                    visit[nr][nc] = true;
                                }
                            }
                        }
                        //Flood Fill을 통해 조각들이 모여 유물이 되고 사라지는지 확인
                        if(trace.size() >= 3) {
                            //유물이 되어 사라지는 경우 가치를 더해주고 조각이 비어있음을 뜻하는 0으로 바꿔줌
                            score += trace.size();
                            while(!trace.isEmpty()) {
                                int[] t = trace.poll();
                                a[t[0]][t[1]] = 0;
                            }
                        }
                    }
                }
            }
            return score;
        }

        //조각이 비어있는 곳에 새로운 조각을 채워준다.
        public void fill(Queue<Integer> que) {
            //열이 작은 순으로 -> 열이 같으면 행이 큰 순서
            for(int j = 0; j < N_large; j++) {
                //index는 0번부터이므로 -1
                for(int i = N_large -1; i >= 0; i--) {
                    if(a[i][j] == 0 && !que.isEmpty()) {
                        a[i][j] = que.poll();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int K = sc.nextInt();
        int M = sc.nextInt();
        Queue<Integer> Q = new LinkedList<>();
        Board board = new Board();

        for(int i = 0; i < N_large; i++) {
            for(int j = 0; j < N_large; j++) {
                board.a[i][j] = sc.nextInt();
            }
        }
        for(int i = 0; i < M; i++) {
            Q.offer(sc.nextInt());
        }

        //최대 k번의 탐사 과정을 거친다
        while(K --> 0) {
            int maxScore = 0;
            Board maxScoreBoard = null;
            //회전 목표에 맞는 결과를 maxScoreBoard에 저장
            //1. 유물 1차 획득 가치를 최대화
            //2. 회전한 각도가 가장 작은 방법을 선택
            //3. 회전 중심 좌표의 열이 가장 작은 구간을, 열이 같다면 행이 가장 작은 구간을 선택
            for(int cnt = 1; cnt <= 3; cnt++) {
                for(int dc = 0; dc <= N_large - N_small; dc++) {
                    for(int dr = 0; dr <= N_large - N_small; dr++) {
                        Board rotated = board.rotate(dr, dc, cnt);
                        int score = rotated.calcScore();
                        if(maxScore < score) {
                            maxScore = score;
                            maxScoreBoard = rotated;
                        }
                    }
                }
            }
            //회전을 통해 더 이상 유물 획득할 수 없는 경우 탐사 종료
            if(maxScoreBoard == null) {
                break;
            }
            board = maxScoreBoard;
            //유물의 연쇄 획득을 위해 유물 조각을 채우고 유물을 획득하는 과정을 더이상 획득할 수 있는 유물이 없을 때까지 반복
            while(true) {
                board.fill(Q);
                int newScore = board.calcScore();
                if(newScore == 0) break;
                maxScore += newScore;
            }
            System.out.print(maxScore + " ");
        }
    }
}