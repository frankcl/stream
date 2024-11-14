package xin.manong.stream.test.resource;

import xin.manong.stream.sdk.resource.Resource;

import java.util.Map;

/**
 * 自增ID资源
 *
 * @author frankcl
 * @date 2019-06-01 12:24
 */
class AutoIncreasedIDResource extends Resource<AutoIncreasedIDBuilder> {

    public AutoIncreasedIDResource(String name) {
        super(name);
    }

    @Override
    public AutoIncreasedIDBuilder create(Map<String, Object> configMap) {
        return new AutoIncreasedIDBuilder();
    }

    @Override
    public void destroy() {
        object = null;
    }
}
