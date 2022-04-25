package treeGenerator

object TestString {
    val str = "void level_order(struct node *ptr)\n" +
            "{\n" +
            "int front = 0, rear = 0;\n" + // rear?
            "struct node *queue[MAX_QUEUE_SIZE];\n" +
            "if (!ptr) return;\n" +
            "addq(ptr);\n" +
            "for ( ; ; ) {\n" +
            "ptr = deleteq();\n" +
            "if (ptr == NULL) break;\n" +
            "printf(\"%d\", ptr->data);\n" +
            "if (ptr->left_child)\n" +
            "addq(ptr->left_child);\n" +
            "if (ptr->right_child)\n" +
            "addq(ptr->right_child);\n" +
            "} }"

    val str2 = "int equal(struct node *first, struct node *second)\n" +
            "{\n" +
            "return ((!first && !second) || (first && second &&\n" +
            "(first -> data == second -> data) &&\n" +
            "equal(first -> left_child, second -> left_child) &&\n" +
            "equal(first -> right_child, second -> right_child)));\n" +
            "}"

    val ifstr = "int TreeGenerator.main(void) {\n" +
            "\tint a, b;\n" +
            "\tif(!ptr) {}\n" +
            "\tif ( ( 'A' && ! 22 || 'C' ) && ( 4 && ! ( ! 5 || 6 ) ) && ( 2 || ! 4 && 7 )) {\n" +
            "\t\treturn;\n" +
            "\t}\n" +
            "\telse if (3 + 2) printf(\"Hello\");\n" +
            "\telse  int a;\n" +
            "}"

    val switch = "int TreeGenerator.main(int abc) {\n" +
            "char input = 'A';\n" +
            "switch(input){\n" +
            "    case 'A': \n" +
            "        scanf(\"%s\", &input);\n" +
            "        printf(\"First!.\");\n" +
            "    case 3: \n" +
            "        printf(\"input의 값은 B입니다.\");\n" +
            "        break;  \n" +
            "    default :    \n" +
            "        printf(\"input의 값은 A과B가 아닌 다른 문자입니다.\");\n" +
            "} }"

    val loop1 = "int sum() {\n" +
            "int i = 1;\n" +
            "int sum = 0;\n" +
            "while(i <= 10){\n" +
            "sum = sum + a[100];\n" +
            "a[0]++;\n" +
            "}\n" +
            "}"

    val forStr = "int forTest(){\n" +
            "int i=0;\n" +
            "for(;i<10;i++){\n" +
            "    a = 3;\n" +
            "}\n" +
            "for(int i=100, j = 3;;i++){\n" +
            "    a = 3;\n" +
            "   break;\n" +
            "}\n" +
            "for(int i=0;i<10;){\n" +
            "    a = 3;\n" +
            "}\n" +
            "for(int i=0;i<10;i++)\n" +
            "    a = 3;\n" +
            "}"

    val assign = "int TreeGenerator.main() {\n" +
            "\tint a = 3, b;\n" +
            " b = 3 - 2;\n" +
            " a = ++3 - b-- ;\n" +
            "}"

    val printf = "int TreeGenerator.main() {\n" +
            "\n" +
            "\tprintf(\"The number of cars\\\" : %d\", 3 + 2, b);\n" +
            "struct node *ptr = NULL;" +
            "struct node *temp = (struct node *)malloc(sizeof(node));\n" +
            "}"

    val nestedIf = "int TreeGenerator.main() {\n" +
            "\n" +
            "if(a < b) {\n" +
            "int c = 3;\n" +
            "c = 5;\n" +
            "\tif(b > c) {\n" +
            "\t\tint k = (int)'a';\n" +
            "}\n" +
            "\n" +
            "}\n" +
            "}"

    val multipleFunction = "int add(int a, int b) {\n" +
            "\treturn a+b;\n" +
            "}\n" +
            "\n" +
            "int main() {\n" +
            "int a  = 3, b = 2;\n" +
            "printf(\"%d\\n\", 'a'+b);\n" +
            "return NULL;\n" +
            "}"

    val iterInorder = "void iter_inorder(struct node *node)\n" +
            "{\n" +
            "int top = -1;\n" +
            "struct node *stack[MAX_STACK_SIZE];\n" +
            "for (; ;) {\n" +
            "for (; node; node = node -> left_child)\n" +
            "push(node);\n" +
            "node = pop();\n" +
            "if (!node) break;\n" +
            "printf(\"%d\", node -> data);\n" +
            "node = node -> right_child;\n" +
            "}\n" +
            "}"

    val getLeafNode = "int get_leaf_count(struct node *ptr)\n" +
            "{\n" +
            "int count = 0;\n" +
            "if (ptr != NULL) {\n" +
            "if (ptr -> left_child == NULL &&\n" +
            "ptr -> right_child == NULL)\n" +
            "return 1;\n" +
            "else count = get_leaf_count(ptr -> left_child) +\n" +
            "get_leaf_count(ptr -> right_child);\n" +
            "}\n" +
            "return count;\n" +
            "}"

    val simple = "int main() {\n" +
            "int a = 3 + 3 + (1 < 2);\n" +
            "a = 97;\n" +
            "if(a > 100) printf(\"!!\");" +
            "else if(a < 103) printf(\"??\");" +
            "else printf(\"||\");" +
            "printf(\" a is : %d, %c\", a, 'a' + 2);" +
            "}"

    val simple2 = "int main() {\n" +
            "int i = 3;\n" +
            "while(i < 5) {\n" +
            "printf(\"while\\n\");\n" +
            "i = i + 1;\n" +
            "}\n" +
            "for(int j = 0; j <= 10; ++i, ++j) {\n" +
            "\tprintf(\"%d   %d\\n\", i, j);\n" +
            "}\n" +
            "}"

    val point = "int main() {" +
            "int k = 3;" +
            "int *i = new int;" +
            "*i = 3;" +
            "printf(\"%d\", *i);"+
            "}"

}