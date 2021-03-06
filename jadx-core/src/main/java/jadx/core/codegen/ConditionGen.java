package jadx.core.codegen;

import jadx.core.dex.instructions.ArithNode;
import jadx.core.dex.instructions.IfOp;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.InsnWrapArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.regions.Compare;
import jadx.core.dex.regions.IfCondition;
import jadx.core.utils.ErrorsCounter;
import jadx.core.utils.exceptions.CodegenException;
import jadx.core.utils.exceptions.JadxRuntimeException;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionGen extends InsnGen {
	private static final Logger LOG = LoggerFactory.getLogger(ConditionGen.class);

	public ConditionGen(InsnGen insnGen) {
		super(insnGen.mgen, insnGen.fallback);
	}

	void add(CodeWriter code, IfCondition condition) throws CodegenException {
		switch (condition.getMode()) {
			case COMPARE:
				addCompare(code, condition.getCompare());
				break;

			case NOT:
				addNot(code, condition);
				break;

			case AND:
			case OR:
				addAndOr(code, condition);
				break;

			default:
				throw new JadxRuntimeException("Unknown condition mode: " + condition);
		}
	}

	void wrap(CodeWriter code, IfCondition cond) throws CodegenException {
		boolean wrap = isWrapNeeded(cond);
		if (wrap) {
			code.add('(');
		}
		add(code, cond);
		if (wrap) {
			code.add(')');
		}
	}

	private void addCompare(CodeWriter code, Compare compare) throws CodegenException {
		IfOp op = compare.getOp();
		InsnArg firstArg = compare.getA();
		InsnArg secondArg = compare.getB();
		if (firstArg.getType().equals(ArgType.BOOLEAN)
				&& secondArg.isLiteral()
				&& secondArg.getType().equals(ArgType.BOOLEAN)) {
			LiteralArg lit = (LiteralArg) secondArg;
			if (lit.getLiteral() == 0) {
				op = op.invert();
			}
			if (op == IfOp.EQ) {
				// == true
				addArg(code, firstArg, false);
				return;
			} else if (op == IfOp.NE) {
				// != true
				code.add('!');
				boolean wrap = isWrapNeeded(firstArg);
				if (wrap) {
					code.add('(');
				}
				addArg(code, firstArg, false);
				if (wrap) {
					code.add(')');
				}
				return;
			}
			LOG.warn(ErrorsCounter.formatErrorMsg(mth, "Unsupported boolean condition " + op.getSymbol()));
		}

		addArg(code, firstArg, isWrapNeeded(firstArg));
		code.add(' ').add(op.getSymbol()).add(' ');
		addArg(code, secondArg, isWrapNeeded(secondArg));
	}

	private void addNot(CodeWriter code, IfCondition condition) throws CodegenException {
		code.add('!');
		wrap(code, condition.getArgs().get(0));
	}

	private void addAndOr(CodeWriter code, IfCondition condition) throws CodegenException {
		String mode = condition.getMode() == IfCondition.Mode.AND ? " && " : " || ";
		Iterator<IfCondition> it = condition.getArgs().iterator();
		while (it.hasNext()) {
			wrap(code, it.next());
			if (it.hasNext()) {
				code.add(mode);
			}
		}
	}

	private boolean isWrapNeeded(IfCondition condition) {
		return !condition.isCompare();
	}

	private static boolean isWrapNeeded(InsnArg arg) {
		if (!arg.isInsnWrap()) {
			return false;
		}
		InsnNode insn = ((InsnWrapArg) arg).getWrapInsn();
		if (insn.getType() == InsnType.ARITH) {
			switch (((ArithNode) insn).getOp()) {
				case ADD:
				case SUB:
				case MUL:
				case DIV:
				case REM:
					return false;
			}
		} else if (insn.getType() == InsnType.INVOKE) {
			return false;
		}
		return true;
	}
}
