package jadx.tests.internal.loops;

import jadx.api.InternalJadxTest;
import jadx.core.dex.nodes.ClassNode;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class TestLoopDetection extends InternalJadxTest {

	public static class TestCls {

		private void test(int[] a, int b) {
			int i = 0;
			while (i < a.length && i < b) {
				a[i]++;
				i++;
			}
			while (i < a.length) {
				a[i]--;
				i++;
			}
		}
	}

	@Test
	public void test() {
		ClassNode cls = getClassNode(TestCls.class);
		String code = cls.getCode().toString();
		System.out.println(code);

		assertThat(code, containsString("while (i < a.length && i < b) {"));
		assertThat(code, containsString("while (i < a.length) {"));
	}
}
