# Imagenes estaticas del sitio
Uso:

```html
<img th:src="@{/images/logo-conti-talent.svg}" src="/images/logo-conti-talent.svg" alt="Conti Talent">
```

Carpetas sugeridas:

- `brand/`: logos e isotipos.
- `hero/`: imagenes grandes de portada.
- `banners/`: banners por seccion.
- `icons/`: iconos ilustrativos.
- `gifs/`: animaciones cortas.

Formatos recomendados:

- Logo: SVG si es vectorial, PNG transparente si viene como bitmap.
- Fotos o renders: WebP.
- Imagenes con transparencia: PNG o WebP.
- Animaciones: GIF solo si es corto; WebP animado si se puede.

Tamanos sugeridos:

- Logo horizontal: 512x160 px o SVG.
- Isotipo: 512x512 px o SVG.
- Hero home: 1920x1080 px, WebP.
- Banner seccion: 1600x600 px, WebP.
- Card/thumbnail: 800x600 px, WebP.
- Iconos: 256x256 px, PNG/WebP/SVG.
- GIF pequeno: maximo 900 px de ancho, idealmente menos de 2 MB.
