export async function scrapProduct(productUrl) {
    const response = await fetch(
        `http://localhost:8080/product/scrap?productUrl=${encodeURIComponent(productUrl)}`,
        {
            method: "POST",
        }
    );
     const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message);
    }

    return data;
}
export async function getAnalysis(productId) {
    const response = await fetch(
        `http://localhost:8080/ai/analyze/${productId}`
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message);
    }

    return data;
}