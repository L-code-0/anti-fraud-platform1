package com.anti.fraud.modules.collaboration.algorithm;

import com.anti.fraud.modules.collaboration.model.Operation;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作转换(OT)算法实现
 */
@Slf4j
public class OTAlgorithm {

    /**
     * 转换操作，处理并发操作的冲突
     * @param op1 第一个操作
     * @param op2 第二个操作
     * @return 转换后的操作列表，顺序为 [op1', op2']
     */
    public static List<Operation> transform(Operation op1, Operation op2) {
        if (op1 == null || op2 == null) {
            throw new IllegalArgumentException("操作不能为空");
        }

        List<Operation> result = new ArrayList<>();

        // 处理不同类型的操作组合
        if (op1.getType() == Operation.Type.INSERT) {
            if (op2.getType() == Operation.Type.INSERT) {
                result = transformInsertInsert(op1, op2);
            } else if (op2.getType() == Operation.Type.DELETE) {
                result = transformInsertDelete(op1, op2);
            } else if (op2.getType() == Operation.Type.REPLACE) {
                result = transformInsertReplace(op1, op2);
            }
        } else if (op1.getType() == Operation.Type.DELETE) {
            if (op2.getType() == Operation.Type.INSERT) {
                result = transformDeleteInsert(op1, op2);
            } else if (op2.getType() == Operation.Type.DELETE) {
                result = transformDeleteDelete(op1, op2);
            } else if (op2.getType() == Operation.Type.REPLACE) {
                result = transformDeleteReplace(op1, op2);
            }
        } else if (op1.getType() == Operation.Type.REPLACE) {
            if (op2.getType() == Operation.Type.INSERT) {
                result = transformReplaceInsert(op1, op2);
            } else if (op2.getType() == Operation.Type.DELETE) {
                result = transformReplaceDelete(op1, op2);
            } else if (op2.getType() == Operation.Type.REPLACE) {
                result = transformReplaceReplace(op1, op2);
            }
        }

        log.debug("转换操作: op1={}, op2={} → op1'={}, op2'={}", op1, op2, result.get(0), result.get(1));
        return result;
    }

    /**
     * 转换两个插入操作
     * @param op1 第一个插入操作
     * @param op2 第二个插入操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformInsertInsert(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        if (op1.getPosition() < op2.getPosition()) {
            // op1 在 op2 之前插入，op2 的位置需要后移
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition() + op1.getContent().length(),
                    op2.getContent(),
                    op2.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op1.getPosition() > op2.getPosition()) {
            // op1 在 op2 之后插入，op1 的位置需要后移
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition() + op2.getContent().length(),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else {
            // 两个操作在同一位置插入，按照用户ID排序
            if (op1.getUserId() < op2.getUserId()) {
                Operation transformedOp2 = new Operation(
                        op2.getType(),
                        op2.getPosition() + op1.getContent().length(),
                        op2.getContent(),
                        op2.getLength(),
                        op2.getVersion(),
                        op2.getUserId(),
                        op2.getUserName()
                );
                result.add(op1);
                result.add(transformedOp2);
            } else {
                Operation transformedOp1 = new Operation(
                        op1.getType(),
                        op1.getPosition() + op2.getContent().length(),
                        op1.getContent(),
                        op1.getLength(),
                        op1.getVersion(),
                        op1.getUserId(),
                        op1.getUserName()
                );
                result.add(transformedOp1);
                result.add(op2);
            }
        }

        return result;
    }

    /**
     * 转换插入操作和删除操作
     * @param op1 插入操作
     * @param op2 删除操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformInsertDelete(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        if (op1.getPosition() <= op2.getPosition()) {
            // 插入位置在删除位置之前或相同，删除位置需要后移
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition() + op1.getContent().length(),
                    op2.getContent(),
                    op2.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op1.getPosition() <= op2.getPosition() + op2.getLength()) {
            // 插入位置在删除范围内，插入位置需要前移
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op2.getPosition(),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else {
            // 插入位置在删除范围之后，不需要转换
            result.add(op1);
            result.add(op2);
        }

        return result;
    }

    /**
     * 转换插入操作和替换操作
     * @param op1 插入操作
     * @param op2 替换操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformInsertReplace(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        if (op1.getPosition() <= op2.getPosition()) {
            // 插入位置在替换位置之前或相同，替换位置需要后移
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition() + op1.getContent().length(),
                    op2.getContent(),
                    op2.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op1.getPosition() <= op2.getPosition() + op2.getLength()) {
            // 插入位置在替换范围内，插入位置需要前移
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op2.getPosition(),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else {
            // 插入位置在替换范围之后，不需要转换
            result.add(op1);
            result.add(op2);
        }

        return result;
    }

    /**
     * 转换删除操作和插入操作
     * @param op1 删除操作
     * @param op2 插入操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformDeleteInsert(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        if (op2.getPosition() < op1.getPosition()) {
            // 插入位置在删除位置之前，删除位置需要后移
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition() + op2.getContent().length(),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else if (op2.getPosition() <= op1.getPosition() + op1.getLength()) {
            // 插入位置在删除范围内，删除长度需要增加
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition(),
                    op1.getContent(),
                    op1.getLength() + op2.getContent().length(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else {
            // 插入位置在删除范围之后，不需要转换
            result.add(op1);
            result.add(op2);
        }

        return result;
    }

    /**
     * 转换两个删除操作
     * @param op1 第一个删除操作
     * @param op2 第二个删除操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformDeleteDelete(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        int op1End = op1.getPosition() + op1.getLength();
        int op2End = op2.getPosition() + op2.getLength();

        if (op1End <= op2.getPosition()) {
            // op1 在 op2 之前，op2 的位置需要前移
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition() - op1.getLength(),
                    op2.getContent(),
                    op2.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op2End <= op1.getPosition()) {
            // op2 在 op1 之前，op1 的位置需要前移
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition() - op2.getLength(),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else if (op1.getPosition() <= op2.getPosition() && op2End <= op1End) {
            // op2 完全包含在 op1 中，op1 的长度需要减少
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition(),
                    op1.getContent(),
                    op1.getLength() - op2.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            // op2 被完全包含，不需要执行
            result.add(transformedOp1);
            result.add(null); // 表示op2不需要执行
        } else if (op2.getPosition() <= op1.getPosition() && op1End <= op2End) {
            // op1 完全包含在 op2 中，op2 的长度需要减少
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition(),
                    op2.getContent(),
                    op2.getLength() - op1.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            // op1 被完全包含，不需要执行
            result.add(null); // 表示op1不需要执行
            result.add(transformedOp2);
        } else if (op1.getPosition() <= op2.getPosition() && op1End <= op2End) {
            // op1 和 op2 部分重叠，op2 的位置和长度需要调整
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op1.getPosition(),
                    op2.getContent(),
                    op2End - op1End,
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op2.getPosition() <= op1.getPosition() && op2End <= op1End) {
            // op2 和 op1 部分重叠，op1 的长度需要调整
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op2.getPosition(),
                    op1.getContent(),
                    op1End - op2End,
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        }

        return result;
    }

    /**
     * 转换删除操作和替换操作
     * @param op1 删除操作
     * @param op2 替换操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformDeleteReplace(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        int op1End = op1.getPosition() + op1.getLength();
        int op2End = op2.getPosition() + op2.getLength();

        if (op1End <= op2.getPosition()) {
            // op1 在 op2 之前，op2 的位置需要前移
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition() - op1.getLength(),
                    op2.getContent(),
                    op2.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op2End <= op1.getPosition()) {
            // op2 在 op1 之前，op1 的位置需要前移
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition() - (op2.getContent().length() - op2.getLength()),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else if (op1.getPosition() <= op2.getPosition() && op2End <= op1End) {
            // op2 完全包含在 op1 中，op1 的长度需要调整
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition(),
                    op1.getContent(),
                    op1.getLength() - (op2.getContent().length() - op2.getLength()),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else if (op2.getPosition() <= op1.getPosition() && op1End <= op2End) {
            // op1 完全包含在 op2 中，op2 的长度需要调整
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition(),
                    op2.getContent(),
                    op2.getLength() - op1.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op1.getPosition() <= op2.getPosition() && op1End <= op2End) {
            // op1 和 op2 部分重叠，op2 的位置和长度需要调整
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op1.getPosition(),
                    op2.getContent(),
                    op2End - op1End,
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op2.getPosition() <= op1.getPosition() && op2End <= op1End) {
            // op2 和 op1 部分重叠，op1 的长度需要调整
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op2.getPosition(),
                    op1.getContent(),
                    op1End - op2End,
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        }

        return result;
    }

    /**
     * 转换替换操作和插入操作
     * @param op1 替换操作
     * @param op2 插入操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformReplaceInsert(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        if (op2.getPosition() < op1.getPosition()) {
            // 插入位置在替换位置之前，替换位置需要后移
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition() + op2.getContent().length(),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else if (op2.getPosition() <= op1.getPosition() + op1.getLength()) {
            // 插入位置在替换范围内，替换长度需要增加
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition(),
                    op1.getContent(),
                    op1.getLength() + op2.getContent().length(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else {
            // 插入位置在替换范围之后，不需要转换
            result.add(op1);
            result.add(op2);
        }

        return result;
    }

    /**
     * 转换替换操作和删除操作
     * @param op1 替换操作
     * @param op2 删除操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformReplaceDelete(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        if (op1.getPosition() <= op2.getPosition()) {
            // 替换位置在删除位置之前或相同，删除位置需要后移
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition() + (op1.getContent().length() - op1.getLength()),
                    op2.getContent(),
                    op2.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op1.getPosition() <= op2.getPosition() + op2.getLength()) {
            // 替换位置在删除范围内，替换位置需要前移
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op2.getPosition(),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else {
            // 替换位置在删除范围之后，不需要转换
            result.add(op1);
            result.add(op2);
        }

        return result;
    }

    /**
     * 转换两个替换操作
     * @param op1 第一个替换操作
     * @param op2 第二个替换操作
     * @return 转换后的操作列表
     */
    private static List<Operation> transformReplaceReplace(Operation op1, Operation op2) {
        List<Operation> result = new ArrayList<>();

        int op1End = op1.getPosition() + op1.getLength();
        int op2End = op2.getPosition() + op2.getLength();

        if (op1End <= op2.getPosition()) {
            // op1 在 op2 之前，op2 的位置需要调整
            Operation transformedOp2 = new Operation(
                    op2.getType(),
                    op2.getPosition() + (op1.getContent().length() - op1.getLength()),
                    op2.getContent(),
                    op2.getLength(),
                    op2.getVersion(),
                    op2.getUserId(),
                    op2.getUserName()
            );
            result.add(op1);
            result.add(transformedOp2);
        } else if (op2End <= op1.getPosition()) {
            // op2 在 op1 之前，op1 的位置需要调整
            Operation transformedOp1 = new Operation(
                    op1.getType(),
                    op1.getPosition() + (op2.getContent().length() - op2.getLength()),
                    op1.getContent(),
                    op1.getLength(),
                    op1.getVersion(),
                    op1.getUserId(),
                    op1.getUserName()
            );
            result.add(transformedOp1);
            result.add(op2);
        } else {
            // 两个替换操作重叠，保留最后一个操作
            result.add(null); // 表示op1不需要执行
            result.add(op2);
        }

        return result;
    }

    /**
     * 应用操作到文本
     * @param text 原始文本
     * @param operation 操作
     * @return 应用操作后的文本
     */
    public static String applyOperation(String text, Operation operation) {
        if (text == null) {
            text = "";
        }

        if (operation == null) {
            return text;
        }

        StringBuilder sb = new StringBuilder(text);

        switch (operation.getType()) {
            case INSERT:
                sb.insert(operation.getPosition(), operation.getContent());
                break;
            case DELETE:
                if (operation.getPosition() < sb.length()) {
                    int end = Math.min(operation.getPosition() + operation.getLength(), sb.length());
                    sb.delete(operation.getPosition(), end);
                }
                break;
            case REPLACE:
                if (operation.getPosition() < sb.length()) {
                    int end = Math.min(operation.getPosition() + operation.getLength(), sb.length());
                    sb.replace(operation.getPosition(), end, operation.getContent());
                }
                break;
        }

        return sb.toString();
    }

    /**
     * 应用操作列表到文本
     * @param text 原始文本
     * @param operations 操作列表
     * @return 应用操作后的文本
     */
    public static String applyOperations(String text, List<Operation> operations) {
        if (text == null) {
            text = "";
        }

        if (operations == null || operations.isEmpty()) {
            return text;
        }

        for (Operation operation : operations) {
            if (operation != null) {
                text = applyOperation(text, operation);
            }
        }

        return text;
    }
}
