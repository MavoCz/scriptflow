(async function fetch() {
    const company = await client.get('https://api.coincap.io/v2/assets');
    console.log(company.status);
    const ceoList = await client.get('https://api.coincap.io/v2/assets/bitcoin');
    console.log(ceoList.status);

    return {
        company: company.json(),
        ceos: ceoList.json()
    }
})