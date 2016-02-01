package com.qfree.obo.report.security.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wraps an {@link HttpServletRequest} so that additional request parameters can
 * be added to the request.
 * 
 * <p>
 * The original request must be wrapped because according to the Servlet
 * specification, the map returned by getParameterMap() is immutable.
 * 
 * @author Jeffrey Zelt
 *
 */
public class ModifiableParametersRequestWrapper extends HttpServletRequestWrapper {

	/*
	 * This Map will hold a copy of the parameters that we will add to the
	 * request.
	 */
	private final Map<String, String[]> copyOfExtraParameters;

	/*
	 * This Map will hold both the original parameters from the request that
	 * will be wrapped, as well as the parameters that we will add.
	 * 
	 * However, this Map, which is modifiable, will not be returned by the
	 * wrapped request when its parameterMap is requested because the Map 
	 * returned must be immutable according to the Servlet specification.
	 * Therefore, a different *immutable" Map will be returned that is created
	 * from this mutable Map.
	 */
	private Map<String, String[]> allParameters = null;

	/**
	 * Creates a wrapped request that merges additional parameters into the
	 * request object.
	 * 
	 * @param request
	 * @param extraParams
	 */
	public ModifiableParametersRequestWrapper(final HttpServletRequest request,
			final Map<String, String[]> extraParams) {
		super(request);
		copyOfExtraParameters = new TreeMap<String, String[]>();
		copyOfExtraParameters.putAll(extraParams);
	}

	@Override
	public String getParameter(final String name) {
		String[] strings = getParameterMap().get(name);
		if (strings != null) {
			return strings[0];
		}
		return super.getParameter(name);
	}

	/*
	 * If we add a request parameter that already exists in the original
	 * request, the will REPLACE it with the parameter that we add. This is
	 * because we put "modifiableParameters" into "allParameters" after we put
	 * "super.getParameterMap()" into "allParameters".
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		if (allParameters == null) {
			allParameters = new TreeMap<String, String[]>();
			allParameters.putAll(super.getParameterMap());
			allParameters.putAll(copyOfExtraParameters);
		}
		/*
		 * Return an unmodifiable collection because that is the interface
		 * contract from the Servlet specification.
		 */
		return Collections.unmodifiableMap(allParameters);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(final String name) {
		return getParameterMap().get(name);
	}
}