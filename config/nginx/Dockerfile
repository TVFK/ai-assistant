FROM node:20-alpine AS frontend-builder
RUN apk add --no-cache git
WORKDIR /app
RUN git clone https://github.com/TVFK/rag-frontend . \
    && npm ci && npm run build

FROM nginx:alpine
COPY --from=frontend-builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf