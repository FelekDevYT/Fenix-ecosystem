# FenixAssembly operations

## MOV's

- MOV reg, value
- MOV reg1, reg2

## MATH

- ADD reg1, reg2 -> reg1
- ADD reg, value -> reg
- SUB reg1, reg2 -> reg1
- SUB reg, value -> reg
- MUL reg1, reg2 -> reg1
- MUL reg, value -> reg
- DIV reg1, reg2 -> reg1
- DIV reg, value -> reg

## LOGICAL

- CMP reg1, reg2 -> EQ_F
- CMP reg1, value -> EQ_F
- CMP value, value -> EQ_F
- GT reg1, reg2 -> GT_F
- GT reg1, value -> GT_F
- GT value, value -> GT_F
- LT reg1, reg2 -> LT_F
- LT reg1, value -> LT_F
- LT value, value -> LT_F
- ZERO reg -> ZERO_F
- ZERO value -> ZERO_F

## Labels

```
LABEL lbl:
    ; do_smth
```

## Functions

```
LABEL func:
    ; do_smth
    RET
```

## FLOW

- JMP labelName
- JE(Jump equals) labelName
- JNE(Jump not equals) labelName
- JD(Jump grater than) labelName
- JL(Jump less than) labelName
- JZ(Jump if ZERO_F) labelName
- call(for functions only, with return) funcName

## Variable and memory

- DB varName, value
- MEMWRITE address, reg
- MEMWRITE address, value
- LOAD reg, varName -> loading address of var into reg

## Standard syscalls

- INT sysCall

### calls 
- 0x00:
    R6 - type (0) - print (1) - println (2) - one symbol print
    R7 - address
- 0x01:
    READ CHAR FROM KEYBOARD
    R5 - output
- 0x02:
    R6 - type (0) - shutdown (1) - reset
- 0x03:
    DISK MANAGER
    R6 - type (0) - read (1) - write
    R7 - sector number
    R8 - address from sector for r/w
    R9 - address from RAM to write
    R5 - output of write
    example:
    ```
  mov r6, 1 ;write to disk
  mov r7, 0
  mov r8, 0x2
  mov r9, 0x15 ;'!' character
  int 3
    ```
  
## Stack
R15 - SP

- PUSH val
- POP reg -> reg

## Another functions

- EXTERN "opcodes splitted using ','"
example:
```
extern "0x50, 0x00, 0x5"
```
