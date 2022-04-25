# C-Parser
C Parser written in Kotlin

## 목적
C 언어 코드를 작성해 AST로 만들어주는 프로그램입니다. 복합대입연산자는 지원하지 않습니다.
최종 업데이트 날짜 : 2021/03/12

## 결과
```C
void level_order(struct node* ptr)
{
	int front = 0, rear = 0;
	struct node* queue[MAX_QUEUE_SIZE];
	if (!ptr) return;
	addq(ptr);
	for (; ; ) {
		ptr = deleteq();
		if (ptr == NULL) break;
		printf("%d", ptr->data);
		if (ptr->left_child)
			addq(ptr->left_child);
		if (ptr->right_child)
			addq(ptr->right_child);
	}
}
```
to (level order)
```
(FUNCTION, level_order, link = 2)
(HEADER, null, link = 1)
(ASSIGN, null, link = 2)
(VARIABLE, ptr, link = 0)
(CONSTANT, (struct node *, null), link = 0)
(BODY, null, link = 6)
(ASSIGN, null, link = 2)
(VARIABLE, front, link = 0)
(CONSTANT, (int , (CONSTANT, 0, link = 0)), link = 0)
(ASSIGN, null, link = 2)
(VARIABLE, rear, link = 0)
(CONSTANT, (int , (CONSTANT, 0, link = 0)), link = 0)
(ASSIGN, (VARIABLE, (MAX_QUEUE_SIZE, VARIABLE), link = 0), link = 2)
(VARIABLE, queue, link = 0)
(CONSTANT, (struct node *, null), link = 0)
(CONDITIONAL, if, link = 2)
(HEADER, null, link = 1)
(OPERATOR, (!, NOT), link = 1)
(VARIABLE, (ptr, VARIABLE), link = 0)
(BODY, null, link = 1)
(RETURN, null, link = 1)
(NULL, null, link = 0)
(FUNCTION_CALL, addq, link = 1)
(VARIABLE, (ptr, VARIABLE), link = 0)
(LOOP, for, link = 2)
(HEADER, null, link = 4)
(NULL, null, link = 0)
(NULL, null, link = 0)
(NULL, null, link = 0)
(NULL, null, link = 0)
(BODY, null, link = 5)
(ASSIGN, null, link = 2)
(VARIABLE, ptr, link = 0)
(CONSTANT, (null, (FUNCTION_CALL, deleteq, link = 1)), link = 0)
(CONDITIONAL, if, link = 2)
(HEADER, null, link = 1)
(OPERATOR, (==, EQUALITY), link = 2)
(VARIABLE, (ptr, VARIABLE), link = 0)
(VARIABLE, (NULL, VARIABLE), link = 0)
(BODY, null, link = 1)
(BREAK, null, link = 0)
(IO, printf, link = 2)
(HEADER, "%d", link = 0)
(OPERATOR, (->, STRUCT), link = 2)
(VARIABLE, (ptr, VARIABLE), link = 0)
(VARIABLE, (data, VARIABLE), link = 0)
(CONDITIONAL, if, link = 2)
(HEADER, null, link = 1)
(OPERATOR, (->, STRUCT), link = 2)
(VARIABLE, (ptr, VARIABLE), link = 0)
(VARIABLE, (left_child, VARIABLE), link = 0)
(BODY, null, link = 1)
(FUNCTION_CALL, addq, link = 1)
(OPERATOR, (->, STRUCT), link = 2)
(VARIABLE, (ptr, VARIABLE), link = 0)
(VARIABLE, (left_child, VARIABLE), link = 0)
(CONDITIONAL, if, link = 2)
(HEADER, null, link = 1)
(OPERATOR, (->, STRUCT), link = 2)
(VARIABLE, (ptr, VARIABLE), link = 0)
(VARIABLE, (right_child, VARIABLE), link = 0)
(BODY, null, link = 1)
(FUNCTION_CALL, addq, link = 1)
(OPERATOR, (->, STRUCT), link = 2)
(VARIABLE, (ptr, VARIABLE), link = 0)
(VARIABLE, (right_child, VARIABLE), link = 0)
```
