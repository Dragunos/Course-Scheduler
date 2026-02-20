package vn.edu.haui.scheduler.application.exception;

public final class ExceptionUtils
{
	public static void printFullStack(Throwable t)
	{
		System.err.println("========== ROOT ERROR ==========");

		int level = 0;

		while(t != null) {
			System.err.println("LEVEL " + level);
			System.err.println("TYPE: " + t.getClass().getName());
			System.err.println("MSG : " + t.getMessage());
			System.err.println("-------------------------------");

			for(StackTraceElement e : t.getStackTrace()) {
				System.err.println(e);
			}

			System.err.println();

			t = t.getCause();
			level++;
		}

		System.err.println("================================");
	}
}