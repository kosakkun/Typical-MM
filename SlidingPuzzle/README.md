# Sliding Puzzle

## 問題文
15パズルを知ってますか？あれを```N × M```の盤面でより短い手数で解いてください．

## 制約
4 <= N <= 10  
4 <= M <= 10  

### 入力
盤面の大きさとボードの状態が与えられます．Bi_jには```1 ~ N*M - 1```の数字と，空白を表す```-1```が1つずつ割り当てられます．初期状態から100000回ランダムシャッフルして盤面を生成します．必ず揃えることはできます．
```
N M
B1_1 B1_2 ... B1_M
B2_1 B2_2 ... B2_M
...
BN_1 BN_2 ... BN_M
```

### 出力
入力の盤面を```B1_1 -> B1_2 -> ... -> BN_M-1 -> BN_M```の順番に数字を並び替えるための手順を出力してください．手順のフォーマットは，移動させるパネルの座標(r,c)です．左上の座標を(0,0)，右下の座標を(r-1,c-1)とします．
```
N
r1 c1
r2 c2
...
rN cN
```

## スコア
出力の手順に従ってパズルが解けた場合，その手数をスコアとします．もしパズルが解けていなかったり，指定したパネルの行も列も動かすことができないなど不正な操作があった場合はスコアは```-1```とします．

## テスタ
TopCoder の Marathon Match と同じです．```"<command>"```にプログラムの実行コマンド，```<seed>```に乱数のシードを入れてください．
```sh
$ java -jar tester.jar -exec "<command>" -seed <seed>
```
その他オプション
```
-vis  : ビジュアライズ
```

## 目安？


##  参考文献