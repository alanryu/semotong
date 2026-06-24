import dataclasses as dc


@dc.dataclass(unsafe_hash=True)
class UrlParm:
    key: str
    value: str

# URL 파라미터 생성 함수
def make_url(base_url: str, url_params: list[UrlParm] = None) -> str:
    url = base_url

    if url_params is not None:
        url += '?'
        for i, url_parm in enumerate(url_params):
            url += url_parm.key + '=' + url_parm.value
            if i < len(url_params) - 1:
                url += '&'

    return url