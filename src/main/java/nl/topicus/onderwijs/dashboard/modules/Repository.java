package nl.topicus.onderwijs.dashboard.modules;

public interface Repository<D> {
	public D getProjectData(Project project);
}
